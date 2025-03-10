package io.github.brainage04.twitchplaysminecraft.command.look;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.BlockPos;

public class LookAtCommand {
    public static int execute(FabricClientCommandSource source, BlockPos pos) {
        source.getPlayer().lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, pos.toCenterPos());

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Now looking at %s.".formatted(pos.toShortString()))
                .execute();

        return 1;
    }
}
