package io.github.brainage04.twitchplaysminecraft.util;

public class StringUtils {
    public static String camelToSnakeCase(String input) {
        StringBuilder output = new StringBuilder(input.length());

        output.append(Character.toLowerCase(input.charAt(0)));

        for (int i = 1; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (Character.isUpperCase(currentChar)) {
                output.append("_").append(Character.toLowerCase(currentChar));
            } else {
                output.append(currentChar);
            }
        }

        return output.toString();
    }

    public static String makeFirstLetterUppercase(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    public static String combineStringArray(String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {
            stringBuilder.append(strings[i]);
            if (i < strings.length - 2) {
                stringBuilder.append(", ");
            } else if (i < strings.length - 1) {
                stringBuilder.append(" and ");
            }
        }

        return stringBuilder.toString();
    }
}
