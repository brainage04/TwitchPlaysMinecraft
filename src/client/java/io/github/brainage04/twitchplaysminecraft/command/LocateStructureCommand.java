package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.CommandUtils;
import io.github.brainage04.twitchplaysminecraft.util.EnumUtils;
import io.github.brainage04.twitchplaysminecraft.util.LocateUtils;
import io.github.brainage04.twitchplaysminecraft.util.StringUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.ImportantStructures;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.math.BlockPos;

public class LocateStructureCommand {
    public static int execute(FabricClientCommandSource source, String structureString) {
        if (!CommandUtils.checkPrerequisites(source)) return 0;
        if (source.getWorld() == null) return 0;

        ImportantStructures structure = EnumUtils.getEnumSafely(ImportantStructures.class, structureString);
        if (structure == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.SUCCESS)
                    .text("Success")
                    .execute();

            return 0;
        }
        structureString = StringUtils.snakeCaseToHumanReadable(structure.getName(), true, true);

        // todo
        BlockPos pos = switch (structure) {
            case VILLAGE -> LocateUtils.locateVillage(source.getWorld());
            case LAVA_POOL -> null;
            case BASTION -> null;
            case NETHER_FORTRESS -> null;
            case END_PORTAL -> null;
        };

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
