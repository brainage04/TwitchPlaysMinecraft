package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.getConfig;

public class RenderUtils {
    public static final List<int[]> elementCorners = new ArrayList<>(Arrays.asList(
            new int[]{-1, -1, -1, -1},
            new int[]{-1, -1, -1, -1},
            new int[]{-1, -1, -1, -1},
            new int[]{-1, -1, -1, -1},
            new int[]{-1, -1, -1, -1},
            new int[]{-1, -1, -1, -1},
            new int[]{-1, -1, -1, -1},
            new int[]{-1, -1, -1, -1}
    ));

    public static int[] getCornersWithPadding(int x1, int y1, int x2, int y2) {
        return new int[]{
                x1 - getConfig().elementPadding * 2,
                y1 - getConfig().elementPadding * 2,
                x2 + getConfig().elementPadding * 2,
                y2
        };
    }

    public static boolean mouseInRect(int x1, int y1, int x2, int y2, double mouseX, double mouseY) {
        return (x1 <= mouseX && mouseX <= x2 && y1 <= mouseY && mouseY <= y2);
    }

    public static int getScaledWidth() {
        return MinecraftClient.getInstance().getWindow().getScaledWidth();
    }
    public static int getScaledHeight() {
        return MinecraftClient.getInstance().getWindow().getScaledHeight();
    }
}
