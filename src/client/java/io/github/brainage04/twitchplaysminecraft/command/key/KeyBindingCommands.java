package io.github.brainage04.twitchplaysminecraft.command.key;

import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.KeyUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.KeyBinding;

public class KeyBindingCommands {
    public static int executeHold(FabricClientCommandSource source, String keyName) {
        KeyBinding key = KeyUtils.getKeyBinding(source, keyName);
        if (key == null) return 0;

        new KeyBindingBuilder().source(source).keys(key).execute();

        return 1;
    }

    public static int executeTimedHold(FabricClientCommandSource source, String keyName, int ticks) {
        KeyBinding key = KeyUtils.getKeyBinding(source, keyName);
        if (key == null) return 0;

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Holding key for %d ticks...".formatted(ticks))
                .execute();

        new KeyBindingBuilder().source(source).keys(key)
                .execute();
        new KeyBindingBuilder().source(source).keys(key)
                .pressed(false).extraTickDelay(ticks)
                .execute();

        return 1;
    }

    public static int executeRelease(FabricClientCommandSource source, String keyName) {
        KeyBinding key = KeyUtils.getKeyBinding(source, keyName);
        if (key == null) return 0;

        new KeyBindingBuilder().source(source).keys(key).pressed(false).execute();

        return 1;
    }
}
