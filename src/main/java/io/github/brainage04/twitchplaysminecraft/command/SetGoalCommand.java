package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.ServerFeedbackBuilder;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetGoalCommand {
    public static int execute(ServerCommandSource source, AdvancementEntry advancementEntry) {
        if (advancementEntry == null) {
            new ServerFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("That advancement does not exist! Please try again.")
                    .execute();
            return 0;
        }

        // todo: do something here

        new ServerFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text(Text.literal("Advancement ")
                        .append(advancementEntry.id().toString())
                        .append(" set as new goal."))
                .execute();

        return 1;
    }
}
