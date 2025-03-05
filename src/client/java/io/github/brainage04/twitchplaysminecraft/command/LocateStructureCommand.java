package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.*;
import io.github.brainage04.twitchplaysminecraft.util.enums.ImportantStructures;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.math.BlockPos;

public class LocateStructureCommand {
    public static int execute(FabricClientCommandSource source, String structureString) {
        if (source.getWorld() == null) return 0;

        ImportantStructures structure = EnumUtils.getValueSafely(ImportantStructures.class, structureString);
        if (structure == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("Invalid structure! Valid structures: %s.".formatted(EnumUtils.joinEnumValues(ImportantStructures.class)))
                    .execute();

            return 0;
        }
        structureString = StringUtils.snakeCaseToHumanReadable(structure.getName(), true, true);

        BlockPos pos = structure.function.apply(source);

        if (pos == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("%s could not be found!".formatted(structureString))
                    .execute();

            return 0;
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("%s found at %d, %d, %d.".formatted(structureString, pos.getX(), pos.getY(), pos.getZ()))
                .execute();

        return 1;
    }
}
