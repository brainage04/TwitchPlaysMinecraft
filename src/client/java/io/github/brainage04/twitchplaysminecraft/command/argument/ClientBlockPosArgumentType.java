package io.github.brainage04.twitchplaysminecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.concurrent.CompletableFuture;

public class ClientBlockPosArgumentType implements ArgumentType<BlockPos> {
    public static ClientBlockPosArgumentType blockPos() {
        return new ClientBlockPosArgumentType();
    }

    @Override
    public BlockPos parse(StringReader reader) throws CommandSyntaxException {
        // Parse X coordinate
        int x = reader.readInt();
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
        } else {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                .readerExpectedInt()
                .createWithContext(reader);
        }

        // Parse Y coordinate
        int y = reader.readInt();
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
        } else {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                .readerExpectedInt()
                .createWithContext(reader);
        }

        // Parse Z coordinate
        int z = reader.readInt();

        return new BlockPos(x, y, z);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof FabricClientCommandSource source)) {
            return Suggestions.empty();
        }

        BlockPos pos = new BlockPos(
            source.getPlayer().getBlockX(),
            source.getPlayer().getBlockY(),
            source.getPlayer().getBlockZ()
        );

        String remaining = builder.getRemaining();
        if (remaining.isEmpty()) {
            builder.suggest(pos.getX() + " " + pos.getY() + " " + pos.getZ());
        } else if (!remaining.contains(" ")) {
            builder.suggest(remaining + " " + pos.getY() + " " + pos.getZ());
        } else if (remaining.indexOf(" ") == remaining.lastIndexOf(" ")) {
            builder.suggest(remaining + " " + pos.getZ());
        }

        return builder.buildFuture();
    }

    public static BlockPos getBlockPos(CommandContext<FabricClientCommandSource> context, String name) {
        return context.getArgument(name, BlockPos.class);
    }
}