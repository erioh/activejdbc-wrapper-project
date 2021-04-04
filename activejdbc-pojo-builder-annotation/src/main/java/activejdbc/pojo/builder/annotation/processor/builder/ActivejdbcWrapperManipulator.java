package activejdbc.pojo.builder.annotation.processor.builder;

import org.javalite.activejdbc.Model;

public class ActivejdbcWrapperManipulator {
     public <T extends Model, E extends ActivejdbcWrapper<T>> T getActivejdbcObject(E wrapper) {
          return wrapper.getActivejdbcObject();
     }
     public <T extends Model, E extends ActivejdbcWrapper<T>> void setActivejdbcObject(E wrapper, T model) {
          wrapper.setActivejdbcObject(model);
     }
}
