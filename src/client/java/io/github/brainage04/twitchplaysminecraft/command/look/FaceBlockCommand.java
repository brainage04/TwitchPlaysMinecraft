package io.github.brainage04.twitchplaysminecraft.command.look;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.BlockUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class FaceBlockCommand {
    public static int execute(FabricClientCommandSource source, String blockString) {
        blockString = blockString.toLowerCase();
        Block block = Registries.BLOCK.get(Identifier.of(blockString));
        if (block == Blocks.AIR && !blockString.equals("air")) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No such block \"%s\" exists!".formatted(blockString))
                    .execute();
            return 0;
        }

        // process for getting nearest block of specific type:
        // scan reachable area first (RxRxR, R=3, x2+1 = 7, so 7x7x7)
        // if nothing is found, increment R until 20 is reached (debug screen raycast distance)
        int radius = 3;
        Vec3d pos = BlockUtils.searchCuboid(source.getWorld(), source.getPlayer(), block, radius, false);
        if (pos == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.INFO)
                    .text(Text.literal("No ")
                            .append(block.getName())
                            .append(" block found within 3 blocks. Checking up to 20 blocks..."))
                    .execute();

            radius++;
            while (radius <= 20) {
                pos = BlockUtils.searchCuboid(source.getWorld(), source.getPlayer(), block, radius, true);
                if (pos != null) break;

                radius++;
            }
        }

        if (pos == null) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text(Text.literal("No ")
                            .append(block.getName())
                            .append(" block found within 20 blocks!"))
                    .execute();
            return 0;
        }

        source.getPlayer().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, pos);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text(Text.literal("Now looking at ")
                        .append(block.getName())
                        .append("."))
                .execute();

        return 1;
    }
}
