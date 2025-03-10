package io.github.brainage04.twitchplaysminecraft.command.move;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.util.BlockUtils;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

// i can't believe i got this to work either
public class MoveToCommand {
    private static boolean isRunning = false;
    private static List<BlockPos> path = null;
    private static int pathIndex = 0;

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!isRunning) return;
            if (path == null || path.isEmpty()) return;
            if (client.player == null) return;

            if (pathIndex >= path.size()) {
                stop(SourceUtils.getSource(client.player));
                return;
            }

            Vec3d currentPos = client.player.getPos();
            Vec3d nextPos = path.get(pathIndex).toCenterPos();

            if (currentPos.squaredDistanceTo(nextPos) <= 1) {
                pathIndex++;
            }

            client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(nextPos.getX(), client.player.getEyeY(), nextPos.getZ()));

            client.options.forwardKey.setPressed(true);
        });
    }

    public static void stop(FabricClientCommandSource source) {
        isRunning = false;

        source.getClient().options.forwardKey.setPressed(false);

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.SUCCESS)
                .text("Stopped moving.")
                .execute();

    }

    public static int execute(FabricClientCommandSource source, BlockPos destination) {
        while (!BlockUtils.isWalkable(destination, source.getWorld())) {
            destination = destination.add(0, 1, 0);
        }

        path = FindPathCommand.findPath(source.getPlayer().getBlockPos(), destination, source.getWorld());

        if (path == null || path.isEmpty()) {
            new ClientFeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("Path could not be found!")
                    .execute();

            return 0;
        }

        new ClientFeedbackBuilder().source(source)
                .messageType(MessageType.INFO)
                .text("Moving to %s...".formatted(destination.toShortString()))
                .execute();

        //source.getClient().options.sprintKey.setPressed(true);
        source.getClient().options.getAutoJump().setValue(true);

        isRunning = true;
        pathIndex = 0;

        return 1;
    }

    public static boolean isRunning() {
        return isRunning;
    }
}