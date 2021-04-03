package activejdbc.pojo.builder.annotation.processor.util;

import org.junit.Test;

public class StringTemplatesTest {

    @Test
    public void test() {
        System.out.println(StringTemplates.buildGetter("getSomething", "java.lang.Long", "object", "TEST_COLUMN"));
    }

    @Test
    public void test2() {
        System.out.println(StringTemplates.buildSetter("setSomething", "java.lang.Long", "property", "object", "TEST_COLUMN"));
    }

}