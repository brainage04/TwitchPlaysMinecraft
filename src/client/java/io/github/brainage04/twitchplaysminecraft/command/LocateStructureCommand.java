package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.*;
import io.github.brainage04.twitchplaysminecraft.util.enums.ImportantStructures;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.math.BlockPos;

public class LocateStructureCommand {
    public static int execute(FabricClientCommandSource source, String structureString) {
        structureString = structureString.toLowerCase();
        ImportantStructures structure = EnumUtils.getValueSafely(ImportantStructures.class, structureString);
        if (structure == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("Invalid structure! Valid structures: %s.".formatted(EnumUtils.joinEnumValues(ImportantStructures.class)))
                    .execute();

            return 0;
        }
        structureString = StringUtils.snakeCaseToHumanReadable(structureString, true, false);

        BlockPos pos = structure.function.apply(source);

        if (pos == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("%s could not be found within any loaded chunks!".formatted(structureString))
                    .execute();

            return 0;
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Potential %s found at %s.".formatted(structureString, pos.toShortString()))
                .execute();

        return 1;
    }
}
