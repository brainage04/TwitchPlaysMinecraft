package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@SuppressWarnings("SameReturnValue")
public class ReleaseAllKeysCommand {
    public static int execute(FabricClientCommandSource source, boolean printLogs) {
        new KeyBindingBuilder().source(source)
                .keys(source.getClient().options.allKeys)
                .pressed(false)
                .printLogs(false)
                .execute();

        if (printLogs) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("All keys released.")
                    .execute();
        }

        return 1;
    }

    public static void execute(FabricClientCommandSource source) {
        execute(source, false);
    }
}
