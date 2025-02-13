package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.MessageType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

public class KeyBindingCommand {
    private static final Map<String, Function<GameOptions, KeyBinding>> KEY_BINDINGS = new LinkedHashMap<>(
            Map.ofEntries(
                    entry("attack", o -> o.attackKey),
                    entry("use", o -> o.useKey),
                    entry("forward", o -> o.forwardKey),
                    entry("left", o -> o.leftKey),
                    entry("back", o -> o.backKey),
                    entry("right", o -> o.rightKey),
                    entry("jump", o -> o.jumpKey),
                    entry("sneak", o -> o.sneakKey),
                    entry("sprint", o -> o.sprintKey),
                    entry("drop", o -> o.dropKey),
                    entry("inventory", o -> o.inventoryKey),
                    entry("pickItem", o -> o.pickItemKey),
                    entry("togglePerspective", o -> o.togglePerspectiveKey),
                    entry("swapHands", o -> o.swapHandsKey)
            )
    );

    private static KeyBinding getKeyBinding(FabricClientCommandSource source, String keyName) {
        Function<GameOptions, KeyBinding> keyFunction = KEY_BINDINGS.get(keyName);

        if (keyFunction != null) {
            return keyFunction.apply(source.getClient().options);
        } else {
            String validKeys = String.join(", ", KEY_BINDINGS.keySet());
            new FeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No such key \"%s\" found! Valid keys: %s.".formatted(keyName, validKeys))
                    .execute();
            return null;
        }
    }

    public static int executeHold(FabricClientCommandSource source, String keyName) {
        KeyBinding key = getKeyBinding(source, keyName);
        if (key == null) return 0;

        new KeyBindingBuilder().source(source).keys(key).execute();

        return 1;
    }

    public static int executeTimedHold(FabricClientCommandSource source, String keyName, int ticks) {
        KeyBinding key = getKeyBinding(source, keyName);
        if (key == null) return 0;

        new FeedbackBuilder().source(source)
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
        KeyBinding key = getKeyBinding(source, keyName);
        if (key == null) return 0;

        new KeyBindingBuilder().source(source).keys(key).pressed(false).execute();

        return 1;
    }
}
