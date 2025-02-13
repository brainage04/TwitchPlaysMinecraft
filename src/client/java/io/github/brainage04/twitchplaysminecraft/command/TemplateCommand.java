package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class TemplateCommand {
    public static int execute(FabricClientCommandSource source) {
        // do stuff here

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Success")
                .execute();

        return 1;
    }
}
