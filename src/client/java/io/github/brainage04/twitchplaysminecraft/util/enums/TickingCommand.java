package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.command.CraftCommand;
import io.github.brainage04.twitchplaysminecraft.command.attack.AttackCommand;
import io.github.brainage04.twitchplaysminecraft.command.mine.MineCommand;
import io.github.brainage04.twitchplaysminecraft.command.mine.StripMineCommand;
import io.github.brainage04.twitchplaysminecraft.command.move.MoveCommand;
import io.github.brainage04.twitchplaysminecraft.command.screen.MoveItemCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.BridgeCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.JumpPlaceCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.UseCommand;
import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.function.Consumer;
import java.util.function.Supplier;

public enum TickingCommand implements NamedEnum {
    ATTACK(AttackCommand::isRunning, AttackCommand::stop),
    BRIDGE(BridgeCommand::isRunning, BridgeCommand::stop),
    CRAFT(CraftCommand::isRunning, CraftCommand::stop),
    JUMPPLACE(JumpPlaceCommand::isRunning, JumpPlaceCommand::stop),
    MINE(MineCommand::isRunning, MineCommand::stop),
    STRIPMINE(StripMineCommand::isRunning, StripMineCommand::stop),
    MOVE(MoveCommand::isRunning, MoveCommand::stop),
    MOVEITEM(MoveItemCommand::isRunning, MoveItemCommand::stop),
    USE(UseCommand::isRunning, UseCommand::stop);

    public final Supplier<Boolean> isRunning;
    public final Consumer<FabricClientCommandSource> stop;

    TickingCommand(Supplier<Boolean> isRunning, Consumer<FabricClientCommandSource> stop) {
        this.isRunning = isRunning;
        this.stop = stop;
    }
}
