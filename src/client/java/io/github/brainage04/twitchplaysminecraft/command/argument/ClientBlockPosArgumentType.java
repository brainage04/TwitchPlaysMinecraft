package io.github.brainage04.twitchplaysminecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.math.BlockPos;

public class ClientBlockPosArgumentType implements ArgumentType<BlockPos> {
    private ClientBlockPosArgumentType() {}

    public static ClientBlockPosArgumentType blockPos() {
        return new ClientBlockPosArgumentType();
    }

    @Override
    public BlockPos parse(StringReader reader) throws CommandSyntaxException {
        int x = reader.readInt();
        reader.skipWhitespace();
        int y = reader.readInt();
        reader.skipWhitespace();
        int z = reader.readInt();
        return new BlockPos(x, y, z);
    }

    public static BlockPos getBlockPos(CommandContext<?> context, String name) {
        return context.getArgument(name, BlockPos.class);
    }
}