package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.command.craft.CraftCommand;
import io.github.brainage04.twitchplaysminecraft.command.attack.KillMobCommands;
import io.github.brainage04.twitchplaysminecraft.command.mine.MineCommand;
import io.github.brainage04.twitchplaysminecraft.command.mine.StripMineCommand;
import io.github.brainage04.twitchplaysminecraft.command.move.MoveDirectionCommands;
import io.github.brainage04.twitchplaysminecraft.command.move.MoveToCommand;
import io.github.brainage04.twitchplaysminecraft.command.screen.MoveItemCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.BridgeCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.JumpPlaceCommand;
import io.github.brainage04.twitchplaysminecraft.command.use.UseItemCommand;
import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.function.Consumer;
import java.util.function.Supplier;

public enum TickingCommand implements NamedEnum {
    KILLMOB(KillMobCommands::isRunning, KillMobCommands::stop),
    BRIDGE(BridgeCommand::isRunning, BridgeCommand::stop),
    CRAFT(CraftCommand::isRunning, source -> CraftCommand.stop()),
    JUMPPLACE(JumpPlaceCommand::isRunning, JumpPlaceCommand::stop),
    MINE(MineCommand::isRunning, MineCommand::stop),
    STRIPMINE(StripMineCommand::isRunning, StripMineCommand::stop),
    MOVE(MoveDirectionCommands::isRunning, MoveDirectionCommands::stop),
    MOVETO(MoveToCommand::isRunning, MoveToCommand::stop),
    MOVEITEM(MoveItemCommand::isRunning, source -> MoveItemCommand.stop()),
    USEITEM(UseItemCommand::isRunning, source -> UseItemCommand.stop());

    public final Supplier<Boolean> isRunningSupplier;
    public final Consumer<FabricClientCommandSource> stopConsumer;

    TickingCommand(Supplier<Boolean> isRunningSupplier, Consumer<FabricClientCommandSource> stopConsumer) {
        this.isRunningSupplier = isRunningSupplier;
        this.stopConsumer = stopConsumer;
    }

    public boolean isRunning() {
        return isRunningSupplier.get();
    }

    public void stop(FabricClientCommandSource source) {
        stopConsumer.accept(source);
    }
}
