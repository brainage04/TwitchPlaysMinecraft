package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.MessageType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class ReleaseAllKeysCommand {
    public static int execute(FabricClientCommandSource source) {
        new KeyBindingBuilder().source(source).keys(source.getClient().options.allKeys).printLogs(false).execute();

        new FeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("All keys released.")
                .execute();

        return 1;
    }
}
