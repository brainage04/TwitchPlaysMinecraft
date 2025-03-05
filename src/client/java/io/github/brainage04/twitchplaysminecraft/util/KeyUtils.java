package io.github.brainage04.twitchplaysminecraft.util;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

public class KeyUtils {
    private static final Map<String, Function<GameOptions, KeyBinding>> KEYS = new LinkedHashMap<>(
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

    public static KeyBinding getKeyBinding(FabricClientCommandSource source, String keyName) {
        Function<GameOptions, KeyBinding> function = KEYS.get(keyName);

        if (function != null) {
            return function.apply(source.getClient().options);
        } else {
            String validKeys = String.join(", ", KEYS.keySet());
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Invalid key! Valid keys: %s.".formatted(validKeys))
                    .execute();
            return null;
        }
    }
}
