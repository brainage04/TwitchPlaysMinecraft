package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.util.math.Vec3d;

public class MathUtils {
    public static double distanceToSquared(Vec3d first, Vec3d second) {
        double x = first.x - second.x;
        double y = first.y - second.y;
        double z = first.z - second.z;

        return x * x + y * y + z * z;
    }

    // from https://stackoverflow.com/a/9354899
    public static byte getBit(int number, int position) {
        return (byte) ((number >> position) & 1);
    }

    public static boolean isBitOn(int number, int position) {
        return getBit(number, position) == 1;
    }
}
