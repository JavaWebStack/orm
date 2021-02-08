package org.javawebstack.orm.util;

public class Helper {

    public static String toSnakeCase(String source) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toLowerCase(source.charAt(0)));
        for (int i = 1; i < source.length(); i++) {
            if (Character.isUpperCase(source.charAt(i))) {
                if (!Character.isUpperCase(source.charAt(i - 1)))
                    sb.append("_");
                sb.append(Character.toLowerCase(source.charAt(i)));
            } else {
                sb.append(source.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String pascalToCamelCase(String source) {
        return Character.toLowerCase(source.charAt(0)) + source.substring(1);
    }

}
