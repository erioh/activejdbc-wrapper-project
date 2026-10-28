/*
Copyright 2021-2021 Serhii Demenkov
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package activejdbc.wrapper.query;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.ModelDelegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base of the query classes generated inside wrappers, e.g. {@code FancyTableWrapper.query()}. Conditions are joined
 * with AND unless {@link #or()} is called in between; {@link #group(Function)} puts conditions into parentheses.
 * Sorting is set per column with {@link Condition#asc()} / {@link Condition#desc()}, paging with {@link #limit(long)}
 * and {@link #offset(long)}.
 * <p>
 * Queries run through {@link ModelDelegate} with the model class passed explicitly, so they work with and without
 * ActiveJDBC instrumentation.
 *
 * @param <Q> the generated query class itself
 * @param <M> the ActiveJDBC model
 * @param <W> the wrapper of the model
 */
public abstract class AbstractQuery<Q extends AbstractQuery<Q, M, W>, M extends Model, W> {
    private final Class<M> modelClass;
    private final Function<M, W> wrapperFactory;
    private final StringBuilder sql = new StringBuilder();
    private final List<Object> params = new ArrayList<>();
    private String pendingConnector;
    private final List<String> orderBy = new ArrayList<>();
    private Long limit;
    private Long offset;

    /**
     * Constructor for the generated query classes.
     *
     * @param modelClass     the ActiveJDBC model to query
     * @param wrapperFactory wraps a found model, e.g. {@code FancyTableWrapper::new}
     */
    protected AbstractQuery(Class<M> modelClass, Function<M, W> wrapperFactory) {
        this.modelClass = modelClass;
        this.wrapperFactory = wrapperFactory;
    }

    /**
     * Joins the previous and the next condition with AND. Same as not calling anything between them.
     *
     * @return this query
     */
    public Q and() {
        return connector("AND");
    }

    /**
     * Joins the previous and the next condition with OR. As in SQL, AND binds tighter than OR; use groups to change it.
     *
     * @return this query
     */
    public Q or() {
        return connector("OR");
    }

    /**
     * Shortcut for {@code and().group(group)}.
     *
     * @param group adds the conditions to put in parentheses to the query it's given
     * @return this query
     */
    public Q and(Function<Q, Q> group) {
        return and().group(group);
    }

    /**
     * Shortcut for {@code or().group(group)}.
     *
     * @param group adds the conditions to put in parentheses to the query it's given
     * @return this query
     */
    public Q or(Function<Q, Q> group) {
        return or().group(group);
    }

    /**
     * Adds the conditions built by {@code group} in parentheses, e.g. {@code group(q -> q.a().eq(1).or().b().eq(2))}.
     *
     * @param group adds the conditions to the query it's given and returns it; no sorting or paging inside
     * @return this query
     */
    public Q group(Function<Q, Q> group) {
        // typed as AbstractQuery: private members can't be reached through the type variable Q
        AbstractQuery<?, ?, ?> nested = Objects.requireNonNull(group.apply(newQuery()), "group must return the query it was given");
        nested.checkComplete();
        if (!nested.orderBy.isEmpty() || nested.limit != null || nested.offset != null) {
            throw new IllegalStateException("group() can only contain conditions, not asc()/desc()/limit()/offset()");
        }
        if (nested.sql.length() == 0) {
            throw new IllegalStateException("group() needs at least one condition");
        }
        return append("(" + nested.sql + ")", nested.params);
    }

    /**
     * Returns at most {@code limit} rows. Without sorting the database decides which ones, so it's usually combined
     * with {@link Condition#asc()} / {@link Condition#desc()}.
     *
     * @param limit maximum number of rows, at least 0
     * @return this query
     */
    public Q limit(long limit) {
        this.limit = requireNotNegative(limit, "limit");
        return self();
    }

    /**
     * Skips the first {@code offset} rows; meant for paging together with {@link #limit(long)} and sorting.
     *
     * @param offset number of rows to skip, at least 0
     * @return this query
     */
    public Q offset(long offset) {
        this.offset = requireNotNegative(offset, "offset");
        return self();
    }

    /**
     * Runs the query.
     *
     * @return wrappers of the matching rows, sorted and limited as requested
     */
    public List<W> fetch() {
        return fetchModels().stream()
                .map(wrapperFactory)
                .collect(Collectors.toList());
    }

    /**
     * Runs the query and returns the models instead of wrappers.
     *
     * @return matching models, sorted and limited as requested
     */
    public LazyList<M> fetchModels() {
        return fetchModels(limit);
    }

    /**
     * Runs the query for one row only: the first one of what {@link #fetch()} would return, sorted and offset as
     * requested. Only that row is read from the database. Without sorting the database decides which row comes first.
     * The query itself is not changed, {@link #fetch()} still returns all rows afterwards.
     *
     * @return the wrapper of the first matching row, or empty if no row matches
     */
    public Optional<W> fetchFirst() {
        if (limit != null && limit == 0) {
            return Optional.empty();
        }
        LazyList<M> models = fetchModels(1L);
        return models.isEmpty() ? Optional.empty() : Optional.of(wrapperFactory.apply(models.get(0)));
    }

    private LazyList<M> fetchModels(Long limit) {
        checkComplete();
        LazyList<M> models = sql.length() == 0
                ? ModelDelegate.findAll(modelClass)
                : ModelDelegate.where(modelClass, sql.toString(), params.toArray());
        if (!orderBy.isEmpty()) {
            models = models.orderBy(getOrderBy());
        }
        if (limit != null) {
            models = models.limit(limit);
        }
        if (offset != null) {
            models = models.offset(offset);
        }
        return models;
    }

    /**
     * Counts the matching rows.
     *
     * @return number of all matching rows; sorting, {@link #limit(long)} and {@link #offset(long)} don't apply
     */
    public Long fetchCount() {
        checkComplete();
        return sql.length() == 0
                ? ModelDelegate.count(modelClass)
                : ModelDelegate.count(modelClass, sql.toString(), params.toArray());
    }

    /**
     * Shows the conditions, for debugging and tests.
     *
     * @return the WHERE clause built so far, with {@code ?} placeholders for {@link #getParams()}
     */
    public String getSql() {
        return sql.toString();
    }

    /**
     * Shows the sorting, for debugging and tests.
     *
     * @return the ORDER BY clause built so far, e.g. {@code AMOUNT DESC, FIRST_NAME ASC}; empty without sorting
     */
    public String getOrderBy() {
        return String.join(", ", orderBy);
    }

    /**
     * Shows the parameters, for debugging and tests.
     *
     * @return values for the placeholders in {@link #getSql()}, already converted for JDBC
     */
    public List<Object> getParams() {
        return Collections.unmodifiableList(params);
    }

    /**
     * Used by the generated column methods.
     *
     * @param column the column name
     * @param <T>    the column's Java type
     * @return a condition on the column
     */
    protected <T> Condition<Q, T> newCondition(String column) {
        return new Condition<>(self(), column);
    }

    /**
     * Used by the generated column methods of String columns.
     *
     * @param column the column name
     * @return a condition on the column, with LIKE
     */
    protected StringCondition<Q> newStringCondition(String column) {
        return new StringCondition<>(self(), column);
    }

    /**
     * Creates an empty query of the same type, used for groups.
     *
     * @return a new query
     */
    protected abstract Q newQuery();

    Q append(String expression, List<?> values) {
        if (sql.length() > 0) {
            sql.append(' ').append(pendingConnector == null ? "AND" : pendingConnector).append(' ');
        }
        sql.append(expression);
        params.addAll(values);
        pendingConnector = null;
        return self();
    }

    Q addOrder(String column, String direction) {
        if (pendingConnector != null) {
            throw new IllegalStateException(direction.toLowerCase() + "() follows " + pendingConnector.toLowerCase() + "(), add a condition between them");
        }
        orderBy.add(column + " " + direction);
        return self();
    }

    private static long requireNotNegative(long value, String method) {
        if (value < 0) {
            throw new IllegalArgumentException(method + "() needs a value >= 0, got " + value);
        }
        return value;
    }

    private Q connector(String connector) {
        String method = connector.toLowerCase() + "()";
        if (sql.length() == 0) {
            throw new IllegalStateException(method + " needs a condition before it");
        }
        if (pendingConnector != null) {
            throw new IllegalStateException(method + " follows " + pendingConnector.toLowerCase() + "(), add a condition between them");
        }
        pendingConnector = connector;
        return self();
    }

    private void checkComplete() {
        if (pendingConnector != null) {
            throw new IllegalStateException("the query ends with " + pendingConnector.toLowerCase() + "(), add a condition after it");
        }
    }

    @SuppressWarnings("unchecked")
    private Q self() {
        return (Q) this;
    }
}
