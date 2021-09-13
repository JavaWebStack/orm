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

    public static String toCamelCase(String source) {
        if(source == null || source.length() == 0)
            return source;
        StringBuilder sb = new StringBuilder()
                .append(Character.toLowerCase(source.charAt(0)));
        boolean wordFlag = false;
        for(int i=1; i<source.length(); i++) {
            char c = source.charAt(i);
            if(c == '-' || c == '_') {
                wordFlag = true;
                continue;
            }
            if(Character.isUpperCase(c) && Character.isLowerCase(source.charAt(i-1)))
                wordFlag = true;
            sb.append(wordFlag ? Character.toUpperCase(c) : Character.toLowerCase(c));
        }
        return sb.toString();
    }

}
