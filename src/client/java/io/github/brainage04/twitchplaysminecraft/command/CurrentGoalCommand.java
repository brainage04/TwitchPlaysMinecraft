package io.github.brainage04.twitchplaysminecraft.command;

import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class CurrentGoalCommand {
    public static int execute(FabricClientCommandSource source) {
        return GetGoalInfoCommand.execute(source, AdvancementUtils.currentAdvancement);
    }
}
