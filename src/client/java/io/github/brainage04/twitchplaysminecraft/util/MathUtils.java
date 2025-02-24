package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.predicate.NumberRange;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public class MathUtils {
    public static double distanceToSquared(Vec3d first, Vec3d second) {
        double x = first.x - second.x;
        double y = first.y - second.y;
        double z = first.z - second.z;

        return x * x + y * y + z * z;
    }

    // https://stackoverflow.com/a/9354899
    public static byte getBit(int number, int position) {
        return (byte) ((number >> position) & 1);
    }
    public static boolean isBitOn(int number, int position) {
        return getBit(number, position) == 1;
    }

    private static String formatTimePart(int number) {
        return number < 10 ? "0%s" : "%s";
    }

    private static String formatDuration(Number number) {
        int minutes = number.intValue() / 60;
        int seconds = number.intValue() % 60;

        return "%s:%s".formatted(
                formatTimePart(minutes),
                formatTimePart(seconds)
        ).formatted(
                minutes,
                seconds
        );
    }

    private static <T extends Number> String formatRange(NumberRange<T> range, Function<T, String> formatter) {
        return switch ((range.min().isPresent() ? 2 : 0) + (range.max().isPresent() ? 1 : 0)) {
            case 3 -> // Both min and max present (11 in binary)
                    "%s-%s".formatted(formatter.apply(range.min().get()), formatter.apply(range.max().get()));
            case 2 -> // Only min present (10 in binary)
                    "%s or more".formatted(formatter.apply(range.min().get()));
            case 1 -> // Only max present (01 in binary)
                    "%s or less".formatted(formatter.apply(range.max().get()));
            default -> // Neither present (00 in binary)
                    "a certain number of";
        };
    }
    public static <T extends Number> String parseNumberRange(NumberRange<T> range) {
        return formatRange(range, Object::toString);
    }
    public static <T extends Number> String parseNumberRangeRoman(NumberRange<T> range) {
        return formatRange(range, RomanNumber::toRoman);
    }
    public static <T extends Number> String parseNumberRangeDuration(NumberRange<T> range) {
        return formatRange(range, MathUtils::formatDuration);
    }
}
