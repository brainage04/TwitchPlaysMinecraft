package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;

import java.util.function.Supplier;

import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.getConfig;

public enum ToggleableCommand implements NamedEnum {
    COMMANDQUEUE(() -> getConfig().toggleCommandsConfig.enableCommandQueueCommand),
    REGENERATEAUTHURL(() -> getConfig().toggleCommandsConfig.enableRegenerateAuthUrlCommand),
    STOPIT(() -> getConfig().toggleCommandsConfig.enableStopItCommand),

    KILLMOB(() -> getConfig().toggleCommandsConfig.enableKillMobCommand),

    CRAFT(() -> getConfig().toggleCommandsConfig.enableCraftCommand),

    DROP(() -> getConfig().toggleCommandsConfig.enableDropCommands),

    CLEARCURRENTGOAL(() -> getConfig().toggleCommandsConfig.enableClearCurrentGoalCommand),
    GETCURRENTGOAL(() -> getConfig().toggleCommandsConfig.enableGetCurrentGoalCommand),
    GETGOAL(() -> getConfig().toggleCommandsConfig.enableGetGoalCommand),
    SELECTABLEGOALS(() -> getConfig().toggleCommandsConfig.enableSelectableGoalsCommand),
    SELECTCURRENTGOAL(() -> getConfig().toggleCommandsConfig.enableSelectCurrentGoalCommand),

    PRESS(() -> getConfig().toggleCommandsConfig.enablePressKeyCommands),
    RELEASEALLKEYS(() -> getConfig().toggleCommandsConfig.enableReleaseAllKeysCommand),
    TOGGLE(() -> getConfig().toggleCommandsConfig.enableToggleKeyCommands),

    INVENTORY(() -> getConfig().toggleCommandsConfig.enableInventoryCommand),
    OPENINVENTORY(() -> getConfig().toggleCommandsConfig.enableInventoryCommand),
    SWAPHANDS(() -> getConfig().toggleCommandsConfig.enableSwapHandCommand),
    PICKITEM(() -> getConfig().toggleCommandsConfig.enablePickItemCommand),
    PICKBLOCK(() -> getConfig().toggleCommandsConfig.enablePickItemCommand),

    FACEBLOCK(() -> getConfig().toggleCommandsConfig.enableFaceBlockCommand),
    FACEENTITY(() -> getConfig().toggleCommandsConfig.enableFaceEntityCommand),
    FACE(() -> getConfig().toggleCommandsConfig.enableFaceCommand),
    LOOKAT(() -> getConfig().toggleCommandsConfig.enableLookAtCommand),
    LOOKDIRECTION(() -> getConfig().toggleCommandsConfig.enableLookDirectionCommand),
    LOOKSTRAIGHT(() -> getConfig().toggleCommandsConfig.enableLookStraightCommand),

    MINE(() -> getConfig().toggleCommandsConfig.enableMineCommand),
    STRIPMINE(() -> getConfig().toggleCommandsConfig.enableStripMineCommand),

    MOVE(() -> getConfig().toggleCommandsConfig.enableMoveCommand),
    MOVETO(() -> getConfig().toggleCommandsConfig.enableMoveToCommand),

    CLOSESCREEN(() -> getConfig().toggleCommandsConfig.enableCloseScreenCommand),
    MOVEITEM(() -> getConfig().toggleCommandsConfig.enableMoveItemCommand),
    QUICKMOVE(() -> getConfig().toggleCommandsConfig.enableQuickMoveCommand),

    BRIDGE(() -> getConfig().toggleCommandsConfig.enableBridgeCommand),
    JUMPPLACE(() -> getConfig().toggleCommandsConfig.enableJumpPlaceCommand),
    USE(() -> getConfig().toggleCommandsConfig.enableUseCommand),

    HOTBAR(() -> getConfig().toggleCommandsConfig.enableHotbarCommand),
    LOCATESTRUCTURE(() -> getConfig().toggleCommandsConfig.enableLocateStructureCommand);

    public final Supplier<Boolean> enabledSupplier;

    ToggleableCommand(Supplier<Boolean> enabledSupplier) {
        this.enabledSupplier = enabledSupplier;
    }

    public boolean isEnabled() {
        return this.enabledSupplier.get();
    }
}
