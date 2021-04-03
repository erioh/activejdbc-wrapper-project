package activejdbc.pojo.builder.annotation.processor.util;

public final class StringUtils {
    public static String lowerCaseFirstCharacter(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

    public static String buildPropertyName(String activejdbcPropertyName) {
        String value = activejdbcPropertyName.toLowerCase();
        while (value.contains("_")) {
            value = value.replaceFirst("_[a-z]", String.valueOf(Character.toUpperCase(value.charAt(value.indexOf("_") + 1))));
        }
        return value;
    }

    public static String buildMethodName(String activejdbcPropertyName, String prefix) {
        String propertyName = buildPropertyName(activejdbcPropertyName);
        return prefix + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    private StringUtils() {
    }
}