package io.github.brainage04.twitchplaysminecraft.command.commandqueue;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@SuppressWarnings("SameReturnValue")
public class CommandQueueCommands {
    public static int executeAdd(FabricClientCommandSource source, String command) {
        InstalledChatbot.addToCommandQueue(command);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Added command \"%s\" to queue.".formatted(command))
                .execute();

        return 1;
    }

    public static int executeClear(FabricClientCommandSource source) {
        InstalledChatbot.clearCommandQueue();

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Cleared command queue.")
                .execute();

        return 1;
    }

    public static int executeProcess(FabricClientCommandSource source) {
        InstalledChatbot.processCommandQueue(source.getClient());

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Command queue processed.")
                .execute();

        return 1;
    }
}
