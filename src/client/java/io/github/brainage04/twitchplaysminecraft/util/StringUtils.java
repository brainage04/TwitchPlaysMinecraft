package io.github.brainage04.twitchplaysminecraft.util;

public class StringUtils {
    public static String screamingSnakeCaseToPascalCase(String string) {
        char[] input = string.toCharArray();
        char[] output = new char[input.length];
        output[0] = input[0];

        for (int i = 1; i < string.length(); i++) {
            if (input[i] == '_') {
                output[i] = ' ';
                if (i + 1 < input.length) {
                    i++;
                    output[i] = input[i];
                    continue;
                }
            }

            if (input[i] >= 'A' && input[i] <= 'Z') {
                output[i] = (char) (input[i] | 32); // Bitwise lowercase (A -> a)
            } else {
                output[i] = input[i]; // Non-letters unchanged
            }
        }

        return new String(output);
    }

    public static char toUpperCase(char c) {
        if (c >= 'a' && c <= 'z') {
            return (char) (c & ~32); // Bitwise uppercase (a -> A)
        }

        return c;
    }

    public static String snakeCaseToPascalCase(String string) {
        char[] input = string.toCharArray();
        char[] output = new char[input.length];
        output[0] = toUpperCase(input[0]);

        for (int i = 1; i < string.length(); i++) {
            if (input[i] == '_') {
                output[i] = ' ';
                if (i + 1 < input.length) {
                    i++;
                    output[i] = toUpperCase(input[i]);
                    continue;
                }
            }

            output[i] = input[i];
        }

        return new String(output);
    }
}
