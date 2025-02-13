package io.github.brainage04.twitchplaysminecraft.util;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class SourceUtils {
    public static FabricClientCommandSource getSourceFromClient(MinecraftClient client) {
        return (FabricClientCommandSource) client.player.networkHandler.getCommandSource();
    }

    public static FabricClientCommandSource getSourceFromClient() {
        return (FabricClientCommandSource) MinecraftClient.getInstance().player.networkHandler.getCommandSource();
    }
}
