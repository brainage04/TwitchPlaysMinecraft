package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class HotbarCommand {
    public static int execute(FabricClientCommandSource source, int index) {
        new KeyBindingBuilder().source(source)
                .keys(source.getClient().options.hotbarKeys[index])
                .printLogs(false)
                .execute();

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Slot %d selected.".formatted(index))
                .execute();

        return 1;
    }
}
