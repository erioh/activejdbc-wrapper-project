package activejdbc.pojo.builder.annotation.processor.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class StringTemplatesTest {

    @Test
    public void test() {
        System.out.println(StringTemplates.buildGetter("getSomething", "java.lang.Long", "object", "TEST_COLUMN"));
    }

    @Test
    public void test2() {
        System.out.println(StringTemplates.buildSetter("setSomething", "java.lang.Long", "property", "object", "TEST_COLUMN"));
    }

    @Test
    public void test3() {
        System.out.println(StringTemplates.buildMethodGetObject("Class", "object"));
    }

    @Test
    public void test4() {
        System.out.println(StringTemplates.buildMethodSetObject("Class", "object"));
    }

    @Test
    public void test5() {
        Map<String, String> propertiesAndGetters = new HashMap<>();
        propertiesAndGetters.put("property1", "getProperty1");
        propertiesAndGetters.put("property2", "getProperty2");
        System.out.println(StringTemplates.buildToString(propertiesAndGetters));
    }
}