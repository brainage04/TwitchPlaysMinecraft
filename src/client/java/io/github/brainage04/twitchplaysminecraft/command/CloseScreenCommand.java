package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class CloseScreenCommand {
    public static int execute(FabricClientCommandSource source) {
        if (source.getClient().currentScreen == null) {
            new ClientFeedbackBuilder().source(source)
                    .text("You are not currently using a screen!")
                    .messageType(MessageType.ERROR)
                    .execute();
            return 0;
        }

        source.getClient().execute(() -> source.getClient().currentScreen.close());

        return 1;
    }
}
