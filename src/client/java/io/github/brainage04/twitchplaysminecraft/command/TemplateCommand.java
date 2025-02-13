package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.MessageType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class TemplateCommand {
    public static int execute(FabricClientCommandSource source) {
        // do stuff here

        new FeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Success")
                .execute();

        return 1;
    }
}
