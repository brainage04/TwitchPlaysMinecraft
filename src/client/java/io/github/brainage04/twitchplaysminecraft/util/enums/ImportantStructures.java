package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.LocateUtils;
import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public enum ImportantStructures implements NamedEnum {
    VILLAGE(LocateUtils::locateVillage),
    RUINED_PORTAL(LocateUtils::locateRuinedPortal),
    LAVA_POOL(LocateUtils::locateLavaPool),
    BASTION(LocateUtils::locateBastion),
    NETHER_FORTRESS(LocateUtils::locateNetherFortress),
    END_PORTAL(LocateUtils::locateEndPortal);

    public final Function<FabricClientCommandSource, BlockPos> function;

    ImportantStructures(Function<FabricClientCommandSource, BlockPos> function) {
        this.function = function;
    }
}
