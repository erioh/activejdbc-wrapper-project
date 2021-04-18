package activejdbc.wrapper.annotation.processor.builder;

import org.javalite.activejdbc.Model;

public class ActivejdbcWrapperManipulator {
     public <T extends Model, E extends ActivejdbcWrapper<T>> T getActivejdbcObject(E wrapper) {
          return wrapper.getActivejdbcObject();
     }
}
