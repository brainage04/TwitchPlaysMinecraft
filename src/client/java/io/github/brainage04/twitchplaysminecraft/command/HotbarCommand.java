package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public class HotbarCommand {
    public static int execute(FabricClientCommandSource source, int index) {
        if (index < 0 | index > 8) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Index %d out of bounds (Bounds: 0-8)".formatted(index))
                    .execute();

            return 0;
        }

        KeyBinding key = source.getClient().options.hotbarKeys[index];

        key.timesPressed++;

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text(Text.translatable(key.getTranslationKey())
                        .append(" selected."))
                .execute();

        return 1;
    }
}
