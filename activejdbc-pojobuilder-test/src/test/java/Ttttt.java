import activejdbc.pojo.builder.annotation.processor.util.StringTemplates;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Ttttt {
    @Test
    public void test5() {
        Map<String, String> propertiesAndGetters = new HashMap<>();
        propertiesAndGetters.put("property1", "getProperty1");
        propertiesAndGetters.put("property2", "getProperty2");
        System.out.println(StringTemplates.buildToString(propertiesAndGetters));
    }
}
