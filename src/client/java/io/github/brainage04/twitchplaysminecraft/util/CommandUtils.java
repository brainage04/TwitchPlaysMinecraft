package io.github.brainage04.twitchplaysminecraft.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @SuppressWarnings("ExtractMethodRecommender")
    public static String getMostPopularCommand(List<String> commands) {
        // Map to store command base (without numbers) and their occurrences/info
        Map<String, CommandInfo> commandMap = new HashMap<>();

        // Process each command
        for (String command : commands) {
            String[] parts = command.split(" ");
            // Get base command (all parts except last if it's a number)
            String baseCommand = getBaseCommand(parts);

            commandMap.computeIfAbsent(baseCommand, k -> new CommandInfo())
                    .addCommand(parts);
        }

        // Find the most popular command
        String mostPopular = "";
        int maxOccurrences = 0;

        for (Map.Entry<String, CommandInfo> entry : commandMap.entrySet()) {
            String base = entry.getKey();
            CommandInfo info = entry.getValue();

            if (info.occurrences > maxOccurrences) {
                maxOccurrences = info.occurrences;
                String result = base;
                if (info.hasNumber) {
                    int avg = (int) Math.round((double) info.sum / info.numberCount);
                    result += " " + avg;
                }
                mostPopular = result;
            }
        }

        return mostPopular;
    }

    private static String getBaseCommand(String[] parts) {
        if (parts.length > 1 && isNumeric(parts[parts.length - 1])) {
            return String.join(" ", Arrays.copyOfRange(parts, 0, parts.length - 1));
        }
        return String.join(" ", parts);
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static class CommandInfo {
        int occurrences = 0;
        int sum = 0;
        int numberCount = 0;
        boolean hasNumber = false;

        void addCommand(String[] parts) {
            occurrences++;
            if (parts.length > 1 && isNumeric(parts[parts.length - 1])) {
                hasNumber = true;
                int number = Integer.parseInt(parts[parts.length - 1]);
                sum += number;
                numberCount++;
            }
        }
    }
}
