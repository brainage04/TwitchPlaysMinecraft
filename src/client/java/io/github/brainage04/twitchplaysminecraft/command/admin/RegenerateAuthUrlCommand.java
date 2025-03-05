package io.github.brainage04.twitchplaysminecraft.command.admin;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@SuppressWarnings("SameReturnValue")
public class RegenerateAuthUrlCommand {
    public static int execute(FabricClientCommandSource source) {
        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Restarting installed chatbot...")
                .execute();

        // init installed chatbot on new thread
        // this is done because the game will freeze while the function is running if this method is called on the main thread
        new Thread(InstalledChatbot::regenerate).start();

        return 1;
    }
}
