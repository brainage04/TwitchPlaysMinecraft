package io.github.brainage04.twitchplaysminecraft.command.admin;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.enums.TickingCommand;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@SuppressWarnings("SameReturnValue")
public class StopItCommand {
    public static int execute(FabricClientCommandSource source) {
        int count = 0;

        for (TickingCommand command : TickingCommand.values()) {
            if (command.isRunning()) {
                command.stop(source);
                count++;
            }
        }

        if (count == 0) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text("No commands were running! Nothing changed.")
                    .execute();
        } else {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("Stopped %d currently running commands.".formatted(count))
                    .execute();
        }

        return 1;
    }
}
