package io.github.brainage04.twitchplaysminecraft.command.key;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.RunnableScheduler;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("SameReturnValue")
public class ToggleKeyCommands {
    private static final Set<KeyBinding> toggledKeys = new HashSet<>();

    public static void addKey(KeyBinding key) {
        toggledKeys.add(key);

        resetTicks();
    }

    public static void removeKey(KeyBinding key) {
        toggledKeys.remove(key);
        key.setPressed(false);

        resetTicks();
    }

    public static void removeAllKeys() {
        for (KeyBinding key : toggledKeys) {
            key.setPressed(false);
        }

        toggledKeys.clear();

        resetTicks();
    }

    private static int ticks = 0;

    public static void resetTicks() {
        ticks = 0;
    }

    // IT FINALLY WORKS!!!!!
    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.currentScreen != null) return;

            ticks++;
            if (ticks < RunnableScheduler.MINIMUM_TICK_DELAY) return;

            for (KeyBinding key : toggledKeys) {
                key.setPressed(true);
            }
        });
    }

    public static void toggleKey(FabricClientCommandSource source, KeyBinding key, boolean printLogs) {
        boolean wasPressed = toggledKeys.contains(key);

        if (wasPressed) {
            removeKey(key);
        } else {
            addKey(key);
        }

        if (printLogs) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text(Text.literal("Toggled ")
                            .append(Text.translatable(key.getTranslationKey()))
                            .append(" %s.".formatted(wasPressed ? "off" : "on")))
                    .execute();
        }
    }

    public static void toggleKeys(FabricClientCommandSource source, KeyBinding[] keys, boolean printLogs) {
        for (KeyBinding key : keys) {
            toggleKey(source, key, printLogs);
        }
    }

    public static int execute(FabricClientCommandSource source, KeyBinding key) {
        toggleKey(source, key, true);

        return 1;
    }
}
