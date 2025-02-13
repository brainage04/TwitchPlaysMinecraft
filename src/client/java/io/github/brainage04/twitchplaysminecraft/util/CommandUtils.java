package io.github.brainage04.twitchplaysminecraft.util;

public class CommandUtils {
    public static final int millisecondsBetweenSteps = 100;

    public static Thread currentInteractionThread;

    public static void interruptCurrentInteractionThread() {
        if (currentInteractionThread == null) return;
        if (!currentInteractionThread.isAlive()) return;
        currentInteractionThread.interrupt();
    }

    public static void startNewCurrentInteractionThread(Thread thread) {
        interruptCurrentInteractionThread();
        currentInteractionThread = thread;
        currentInteractionThread.start();
    }
}
