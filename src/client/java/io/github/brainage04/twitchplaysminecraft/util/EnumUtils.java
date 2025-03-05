package io.github.brainage04.twitchplaysminecraft.util;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;

import java.util.Arrays;
import java.util.Objects;

public class EnumUtils {
    public static <T extends Enum<T> & NamedEnum> T getValueSafely(Class<T> enumClass, String name) {
        for (T value : enumClass.getEnumConstants()) {
            if (Objects.equals(value.getName(), name)) return value;
        }

        return null;
    }

    public static <T extends Enum<T> & NamedEnum> String joinEnumValues(Class<T> enumClass) {
        return String.join(
                ", ",
                getEnumNames(enumClass)
        );
    }

    public static <T extends Enum<T> & NamedEnum> String[] getEnumNames(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).map(NamedEnum::getName).toArray(String[]::new);
    }
}
