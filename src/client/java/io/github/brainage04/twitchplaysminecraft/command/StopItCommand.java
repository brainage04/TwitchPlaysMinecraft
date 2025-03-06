package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.enums.TickingCommand;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@SuppressWarnings("SameReturnValue")
public class StopItCommand {
    public static int execute(FabricClientCommandSource source) {
        for (TickingCommand command : TickingCommand.values()) {
            if (command.isRunning()) command.stop(source);
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Stopped all current actions.")
                .execute();

        return 1;
    }
}
