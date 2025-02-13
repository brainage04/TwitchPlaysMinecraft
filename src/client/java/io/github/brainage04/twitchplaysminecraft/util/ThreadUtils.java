package io.github.brainage04.twitchplaysminecraft.util;

public class ThreadUtils {
    public static void sleepSafely(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
