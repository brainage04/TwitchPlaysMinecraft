package io.github.brainage04.twitchplaysminecraft.util;

public class StringUtils {
    public static char toUpperCase(char c) {
        if (c >= 'a' && c <= 'z') {
            return (char) (c & ~32); // Bitwise uppercase (a -> A)
        }

        return c;
    }

    public static String snakeCaseToHumanReadable(String input, boolean insertSpaces, boolean firstCapitalOnly) {
        StringBuilder output = new StringBuilder(input.length());
        output.append(toUpperCase(input.charAt(0)));

        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == '_') {
                if (insertSpaces) output.append(' ');
                if (!firstCapitalOnly && i + 1 < input.length()) {
                    i++;
                    output.append(toUpperCase(input.charAt(i)));
                    continue;
                }
            }

            output.append(input.charAt(i));
        }

        return output.toString();
    }

    public static String pascalCaseToHumanReadable(String input) {
        StringBuilder output = new StringBuilder(input.length());
        output.append(toUpperCase(input.charAt(0)));

        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) >= 'A' && input.charAt(i) <= 'Z') {
                output.append(' ');
            }

            output.append(input.charAt(i));
        }

        return output.toString();
    }
}
