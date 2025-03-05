package io.github.brainage04.twitchplaysminecraft.command.goal;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SetCurrentGoalCommand {
    public static int execute(FabricClientCommandSource source, Identifier advancementId) {
        PlacedAdvancement placedAdvancement = AdvancementUtils.getAdvancementById(advancementId);
        if (placedAdvancement == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Advancement with ID \"%s\" does not exist! Please try again.".formatted(advancementId.toString()))
                    .execute();
            return 0;
        }
        if (!AdvancementUtils.canSelectAdvancement(source.getPlayer(), placedAdvancement)) {
            if (placedAdvancement.getParent() != null) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.ERROR)
                        .text("Prerequisite advancement \"%s\" not met!.".formatted(AdvancementUtils.getAdvancementName(placedAdvancement.getParent())))
                        .execute();
            } else {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.ERROR)
                        .text("Prerequisite not met but this advancement has no parent advancement - this shouldn't happen!")
                        .execute();
            }
            return 0;
        }

        AdvancementUtils.setCurrentAdvancement(placedAdvancement);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text(Text.literal("Advancement ")
                        .append(AdvancementUtils.getAdvancementName(AdvancementUtils.getCurrentAdvancement()))
                        .append(" set as new goal."))
                .execute();

        return 1;
    }
}
