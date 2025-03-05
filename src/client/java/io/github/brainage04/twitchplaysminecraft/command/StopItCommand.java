package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@SuppressWarnings("SameReturnValue")
public class StopItCommand {
    public static int execute(FabricClientCommandSource source) {


        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Stopped current action.")
                .execute();

        return 1;
    }
}
