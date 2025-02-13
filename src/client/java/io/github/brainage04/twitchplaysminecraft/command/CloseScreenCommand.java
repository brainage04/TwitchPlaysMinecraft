package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.MessageType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class CloseScreenCommand {
    public static int execute(FabricClientCommandSource source) {
        if (source.getClient().currentScreen == null) {
            new FeedbackBuilder().source(source)
                    .text("You are not currently using a screen!")
                    .messageType(MessageType.ERROR)
                    .execute();
            return 0;
        }

        source.getClient().execute(() -> source.getClient().currentScreen.close());

        return 1;
    }
}
