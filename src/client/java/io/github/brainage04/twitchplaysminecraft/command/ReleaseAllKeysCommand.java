package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class ReleaseAllKeysCommand {
    public static int execute(FabricClientCommandSource source) {
        new KeyBindingBuilder().source(source).keys(source.getClient().options.allKeys).printLogs(false).execute();

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("All keys released.")
                .execute();

        return 1;
    }
}
