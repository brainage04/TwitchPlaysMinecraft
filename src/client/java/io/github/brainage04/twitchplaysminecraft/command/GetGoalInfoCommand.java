package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.*;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class GetGoalInfoCommand {
    private static void sendCriteriaAndRequirements(FabricClientCommandSource source, PlacedAdvancement placedAdvancement) {
        Map<String, AdvancementCriterion<?>> criteria = placedAdvancement.getAdvancement().criteria();
        List<Map.Entry<String, AdvancementCriterion<?>>> criteriaList = criteria.entrySet().stream().toList();
        for (int i = 0; i < criteria.size(); i++) {
            Map.Entry<String, AdvancementCriterion<?>> entry = criteriaList.get(i);

            String id = entry.getKey();
            AdvancementCriterion<?> criterion = entry.getValue();

            AdvancementCriterionUtils.parseCriterion(id, criterion);

            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text("Criteria %d/%d (%s):".formatted(i + 1, criteria.size(), id))
                    .execute();

            for (MutableText text : AdvancementCriterionUtils.textList) {
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.INFO)
                        .text(Text.literal("> ")
                                .append(text))
                        .execute();
            }
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Requirements: ")
                .execute();
        AdvancementRequirements advancementRequirements = placedAdvancement.getAdvancement().requirements();
        for (int i = 0; i < advancementRequirements.requirements().size(); i++) {
            List<String> list = advancementRequirements.requirements().get(i);

            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text("> %s".formatted(String.join(" OR ", list)))
                    .execute();
        }
    }

    public static int sendGoalFeedback(FabricClientCommandSource source, PlacedAdvancement placedAdvancement) {
        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text(Text.literal("Goal information:").formatted(Formatting.BOLD))
                .execute();

        if (placedAdvancement.getAdvancement().display().isPresent()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.empty()
                            .append(AdvancementUtils.getAdvancementName(placedAdvancement))
                            .append(" - ")
                            .append(placedAdvancement.getAdvancement().display().get().getDescription()))
                    .execute();
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("> ID: %s".formatted(placedAdvancement.getAdvancementEntry().id().toString()))
                .execute();

        //sendCriteriaAndRequirements(source, placedAdvancement);

        return 1;
    }

    public static int execute(FabricClientCommandSource source, Identifier advancementId) {
        PlacedAdvancement placedAdvancement = AdvancementUtils.getAdvancementById(advancementId);
        if (placedAdvancement == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Advancement with ID \"%s\" does not exist! Please try again.".formatted(advancementId.toString()))
                    .execute();
            return 0;
        }

        return sendGoalFeedback(source, placedAdvancement);
    }
}
