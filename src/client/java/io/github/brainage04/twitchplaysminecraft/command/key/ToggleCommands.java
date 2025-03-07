package io.github.brainage04.twitchplaysminecraft.command.key;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

@SuppressWarnings("SameReturnValue")
public class ToggleCommands {
    public static int execute(FabricClientCommandSource source, KeyBinding key) {
        key.setPressed(!key.isPressed());

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text(Text.literal("Toggled ")
                        .append(Text.translatable(key.getTranslationKey()))
                        .append(" %s.".formatted(key.isPressed() ? "on" : "off")))
                .execute();

        return 1;
    }
}
