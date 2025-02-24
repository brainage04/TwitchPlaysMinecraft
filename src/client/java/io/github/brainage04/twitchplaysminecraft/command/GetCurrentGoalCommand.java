package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class GetCurrentGoalCommand {
    public static int execute(FabricClientCommandSource source) {
        if (AdvancementUtils.getCurrentAdvancement() == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No current goal set!")
                    .execute();

            return 0;
        }

        GetGoalInfoCommand.sendGoalFeedback(source, AdvancementUtils.getCurrentAdvancement());

        return 1;
    }
}
