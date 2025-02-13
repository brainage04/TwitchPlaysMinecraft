package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.util.KeyBindingBuilder;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.util.feedback.MessageType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.TreeSet;

import static io.github.brainage04.twitchplaysminecraft.util.BlockPosUtils.compareBlockPos;

public class MineCommand {
    private static final int radius = 8;

    private static boolean isMining = false;
    private static TreeSet<BlockPos> blocks = new TreeSet<>();

    public static void initialize() {
        ClientPlayerBlockBreakEvents.AFTER.register(((clientWorld, clientPlayerEntity, blockPos, blockState) -> {
            if (!isMining) return;

            if (blocks.removeIf(pos -> compareBlockPos(pos, blockPos))) {
                TwitchPlaysMinecraft.LOGGER.info("Block removed");
            } else {
                TwitchPlaysMinecraft.LOGGER.error("No block removed - this shouldn't happen!");
            }

            if (blocks.isEmpty()) {
                isMining = false;
                ReleaseAllKeysCommand.execute(SourceUtils.getSourceFromClient(clientPlayerEntity.client));
                new FeedbackBuilder().source(clientPlayerEntity.client)
                        .messageType(MessageType.SUCCESS)
                        .text("Finished mining blocks.")
                        .execute();
            }
        }));
        
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isMining) return;
            if (client.player == null) return;
            if (client.world == null) return;

            BlockPos desiredBlockPos = blocks.first();
            // todo: look at center of closest visible face instead of just center pos
            client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, desiredBlockPos.toCenterPos());
        });
    }

    public static int execute(FabricClientCommandSource source, String blockName, int count) {
        ClientPlayerEntity player = source.getPlayer();

        Block block = Registries.BLOCK.get(Identifier.of(blockName));

        BlockPos center = player.getBlockPos();
        blocks = new TreeSet<>(Comparator.comparingInt(center::getManhattanDistance));
        for (int x = center.getX() - radius; x < center.getX() + radius; x++) {
            for (int y = center.getY() - radius; y < center.getY() + radius; y++) {
                for (int z = center.getZ() - radius; z < center.getZ() + radius; z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);

                    if (player.clientWorld.getBlockState(blockPos).isOf(block)) {
                        blocks.add(blockPos);
                    }
                }
            }
        }

        while (blocks.size() > count) {
            blocks.removeLast();
        }

        if (blocks.isEmpty()) {
            new FeedbackBuilder().source(source)
                    .messageType(MessageType.ERROR)
                    .text("No blocks found!")
                    .execute();
            return 0;
        }

        isMining = true;
        GameOptions options = source.getClient().options;
        new KeyBindingBuilder().source(source).keys(options.attackKey, options.sneakKey, options.forwardKey).execute();
        new FeedbackBuilder().source(source)
                .messageType(MessageType.ERROR)
                .text(Text.literal("Player is now mining %d ".formatted(count))
                        .append(block.getName())
                        .append("s..."))
                .execute();

        return 1;
    }
}
