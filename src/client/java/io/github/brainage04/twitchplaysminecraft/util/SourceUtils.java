package io.github.brainage04.twitchplaysminecraft.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class SourceUtils {
    // todo: use only player to get command source maybe?
    public static FabricClientCommandSource getSourceFromClient() {
        return (FabricClientCommandSource) MinecraftClient.getInstance().player.networkHandler.getCommandSource();
    }

    public static FabricClientCommandSource getSourceFromClient(MinecraftClient client) {
        return (FabricClientCommandSource) client.player.networkHandler.getCommandSource();
    }

    public static FabricClientCommandSource getSourceFromClient(ClientPlayerEntity player) {
        return (FabricClientCommandSource) player.networkHandler.getCommandSource();
    }
}
