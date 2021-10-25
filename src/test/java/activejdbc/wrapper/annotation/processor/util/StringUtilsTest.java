package activejdbc.wrapper.annotation.processor.util;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(DataProviderRunner.class)
public class StringUtilsTest {

    @DataProvider
    public static Object[][] build_property_name_from_column_name() {
        return new Object[][] {
                {"ADDRESS_LINE_1", "addressLine1"},
                {"ADDRESS_LINE", "addressLine"},
                {"NEW_ADDRESS_LINE", "newAddressLine"},
                {"ADDRESS__LINE", "addressLine"},
                {"A_D_L", "aDL"},
                {"ADDRESS", "address"},
                {"ADDRESS_", "address"},
                {"_ADDRESS", "address"},
        };
    }

    @Test
    @UseDataProvider("build_property_name_from_column_name")
    public void should_build_property_name_from_column_name(String property, String expectedValue) {
        String name = StringUtils.buildPropertyNameFromColumnName(property);
        assertThat(name).isEqualTo(expectedValue);
    }

    @DataProvider
    public static Object[][] lower_case_first_character() {
        return new Object[][] {
                {"A", "a"},
                {"Aa", "aa"},
                {"AAa", "aAa"},
                {"a", "a"},
                {"aA", "aA"},
        };
    }

    @Test
    @UseDataProvider("lower_case_first_character")
    public void should_lower_case_first_character(String property, String expectedValue) {
        assertThat(StringUtils.lowerCaseFirstCharacter(property)).isEqualTo(expectedValue);
    }

    @DataProvider
    public static Object[][] build_method_name() {
        return new Object[][] {
                {"A", "getA"},
                {"Aa", "getAa"},
                {"a", "getA"},
                {"aa", "getAa"},
        };
    }

    @Test
    @UseDataProvider("build_method_name")
    public void should_build_method_name(String property, String expectedValue) {
        assertThat(StringUtils.buildMethodName(property, "get")).isEqualTo(expectedValue);
    }


    @DataProvider
    public static Object[][] is_blank() {
        return new Object[][] {
                {"", true},
                {" ", true},
                {null, true},
                {"1", false}
        };
    }
    @Test
    @UseDataProvider("is_blank")
    public void should_test_is_blank_method(String string, boolean result) {
        assertThat(StringUtils.isBlank(string)).isEqualTo(result);
    }

    @DataProvider
    public static Object[][] is_valid() {
        return new Object[][] {
                {"", false},
                {" ", false},
                {"-", false},
                {"d-d", false},
                {"d*d", false},
                {"dd", true},
                {"dDd", true},
                {"d_Dd", true},
                {" d_Dd   ", true},
                {" 1d_Dd   ", true}
        };
    }
    @Test
    @UseDataProvider("is_valid")
    public void should_test_is_valid_method(String string, boolean result) {
        assertThat(StringUtils.isValid(string)).isEqualTo(result);
    }
}