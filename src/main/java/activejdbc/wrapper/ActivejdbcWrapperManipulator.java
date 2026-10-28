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

package activejdbc.wrapper;

import org.javalite.activejdbc.Model;

/**
 * Gives access to the ActiveJDBC model behind a wrapper, e.g. to save, refresh or delete it. Wrappers keep the model
 * protected, so it doesn't show up in their own API.
 */
public class ActivejdbcWrapperManipulator {

    /**
     * Creates a manipulator; it has no state, one instance can be shared.
     */
    public ActivejdbcWrapperManipulator() {
    }

    /**
     * Returns the model behind a wrapper.
     *
     * @param wrapper a generated wrapper
     * @param <T>     the model type
     * @param <E>     the wrapper type
     * @return the model the wrapper reads and writes; changes through either are visible in both
     */
    public <T extends Model, E extends ActivejdbcWrapper<T>> T getActivejdbcObject(E wrapper) {
        return wrapper.getActivejdbcObject();
    }
}
