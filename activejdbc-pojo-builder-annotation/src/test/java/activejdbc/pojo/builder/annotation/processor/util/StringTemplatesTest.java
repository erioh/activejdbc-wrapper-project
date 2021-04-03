package activejdbc.pojo.builder.annotation.processor.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @Test
    public void test6() {
        String wrappersClassName = "Wrapper";
        Set<String> getters = new HashSet<>();
        getters.add("getProperty1");
        getters.add("getProperty2");
        System.out.println(StringTemplates.buildEquals(wrappersClassName, getters));
    }

    @Test
    public void test7() {
        Set<String> getters = new HashSet<>();
        getters.add("getProperty1");
        getters.add("getProperty2");
        System.out.println(StringTemplates.buildHashCode(getters));
    }

    @Test
    public void test8() {
        String packageName = "com.package";
        String activejdbcObjectClassName = "ActivejdbcClass";
        String wrapperClassName = "ClassWrapper";
        String activejdbcObjectName = "activejdbcClass";
        String methods = "methods();";
        System.out.println(StringTemplates.buildClass(packageName, activejdbcObjectClassName, wrapperClassName, activejdbcObjectName, methods));
    }

}