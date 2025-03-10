package io.github.brainage04.twitchplaysminecraft.command.look;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.enums.LookDirection;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class LookCommands {
    public static int execute(FabricClientCommandSource source, LookDirection lookDirection, int degrees) {
        lookDirection.consumer.accept(source.getPlayer(), degrees);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Now looking %s %d degrees.".formatted(lookDirection.getName(), degrees))
                .execute();

        return 1;
    }
}
