package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class AvailableGoalsCommand {
    public static int execute(FabricClientCommandSource source) {
        List<PlacedAdvancement> availableAdvancements = AdvancementUtils.getAvailableAdvancements();
        if (availableAdvancements.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("There are no goals that can be selected right now - this shouldn't happen!")
                    .execute();

            return 0;
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("The following goals can be selected:").formatted(Formatting.BOLD))
                .execute();

        for (PlacedAdvancement placedAdvancement : availableAdvancements) {
            if (placedAdvancement.getAdvancement().display().isEmpty()) continue;
            AdvancementDisplay advancementDisplay = placedAdvancement.getAdvancement().display().get();

            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.empty().append(AdvancementUtils.getAdvancementName(placedAdvancement))
                            .append(" - ")
                            .append(advancementDisplay.getDescription()))
                    .execute();

            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.literal("> ID: %s".formatted(placedAdvancement.getAdvancementEntry().id().toString())))
                    .execute();
        }

        return 1;
    }
}
