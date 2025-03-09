package io.github.brainage04.twitchplaysminecraft.command.look;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.enums.LookDirection;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class LookCommand {
    public static int execute(FabricClientCommandSource source, LookDirection lookDirection, int degrees) {
        if (source.getClient().player == null) return 0;
        lookDirection.consumer.accept(source.getClient().player, degrees);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Now looking %s %d degrees.".formatted(lookDirection.getName(), degrees))
                .execute();

        return 1;
    }
}
