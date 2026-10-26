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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base of the query classes generated inside wrappers, e.g. {@code FancyTableWrapper.query()}. Conditions are joined
 * with AND unless {@link #or()} is called in between; {@link #group(Function)} puts conditions into parentheses.
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

    protected AbstractQuery(Class<M> modelClass, Function<M, W> wrapperFactory) {
        this.modelClass = modelClass;
        this.wrapperFactory = wrapperFactory;
    }

    /**
     * Joins the previous and the next condition with AND. Same as not calling anything between them.
     */
    public Q and() {
        return connector("AND");
    }

    /**
     * Joins the previous and the next condition with OR. As in SQL, AND binds tighter than OR; use groups to change it.
     */
    public Q or() {
        return connector("OR");
    }

    /**
     * Shortcut for {@code and().group(group)}.
     */
    public Q and(Function<Q, Q> group) {
        return and().group(group);
    }

    /**
     * Shortcut for {@code or().group(group)}.
     */
    public Q or(Function<Q, Q> group) {
        return or().group(group);
    }

    /**
     * Adds the conditions built by {@code group} in parentheses, e.g. {@code group(q -> q.a().eq(1).or().b().eq(2))}.
     */
    public Q group(Function<Q, Q> group) {
        // typed as AbstractQuery: private members can't be reached through the type variable Q
        AbstractQuery<?, ?, ?> nested = Objects.requireNonNull(group.apply(newQuery()), "group must return the query it was given");
        nested.checkComplete();
        if (nested.sql.length() == 0) {
            throw new IllegalStateException("group() needs at least one condition");
        }
        return append("(" + nested.sql + ")", nested.params);
    }

    /**
     * @return wrappers of all matching rows
     */
    public List<W> fetch() {
        return fetchModels().stream()
                .map(wrapperFactory)
                .collect(Collectors.toList());
    }

    /**
     * @return all matching models; ActiveJDBC's {@link LazyList#orderBy(String)}, {@code limit} and {@code offset}
     * can be applied before the list is read
     */
    public LazyList<M> fetchModels() {
        checkComplete();
        return sql.length() == 0
                ? ModelDelegate.findAll(modelClass)
                : ModelDelegate.where(modelClass, sql.toString(), params.toArray());
    }

    /**
     * @return number of matching rows
     */
    public Long fetchCount() {
        checkComplete();
        return sql.length() == 0
                ? ModelDelegate.count(modelClass)
                : ModelDelegate.count(modelClass, sql.toString(), params.toArray());
    }

    /**
     * @return the WHERE clause built so far, with {@code ?} placeholders for {@link #getParams()}
     */
    public String getSql() {
        return sql.toString();
    }

    /**
     * @return values for the placeholders in {@link #getSql()}, already converted for JDBC
     */
    public List<Object> getParams() {
        return Collections.unmodifiableList(params);
    }

    protected <T> Condition<Q, T> newCondition(String column) {
        return new Condition<>(self(), column);
    }

    protected StringCondition<Q> newStringCondition(String column) {
        return new StringCondition<>(self(), column);
    }

    /**
     * @return an empty query of the same type, used for groups
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
