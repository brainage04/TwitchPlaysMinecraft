package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;

import java.util.function.Supplier;

import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.getConfig;

public enum ToggleableCommand implements NamedEnum {
    COMMANDQUEUE(() -> getConfig().toggleCommandsConfig.enableCommandQueueCommand),
    REGENERATEAUTHURL(() -> getConfig().toggleCommandsConfig.enableRegenerateAuthUrlCommand),
    STOPIT(() -> getConfig().toggleCommandsConfig.enableStopItCommand),

    ATTACK(() -> getConfig().toggleCommandsConfig.enableAttackCommand),

    CRAFT(() -> getConfig().toggleCommandsConfig.enableCraftCommand),

    DROP(() -> getConfig().toggleCommandsConfig.enableDropCommand),
    DROPHELDITEM(() -> getConfig().toggleCommandsConfig.enableDropHeldItemCommand),

    CLEARCURRENTGOAL(() -> getConfig().toggleCommandsConfig.enableClearCurrentGoalCommand),
    GETCURRENTGOAL(() -> getConfig().toggleCommandsConfig.enableGetCurrentGoalCommand),
    GETGOAL(() -> getConfig().toggleCommandsConfig.enableGetGoalCommand),
    SELECTABLEGOALS(() -> getConfig().toggleCommandsConfig.enableSelectableGoalsCommand),
    SELECTCURRENTGOAL(() -> getConfig().toggleCommandsConfig.enableSelectCurrentGoalCommand),

    PRESSKEY(() -> getConfig().toggleCommandsConfig.enablePressKeyCommand),
    HOLDKEY(() -> getConfig().toggleCommandsConfig.enableHoldKeyCommand),
    RELEASEKEY(() -> getConfig().toggleCommandsConfig.enableReleaseKeyCommand),
    RELEASEALLKEYS(() -> getConfig().toggleCommandsConfig.enableReleaseAllKeysCommand),

    LOOK(() -> getConfig().toggleCommandsConfig.enableLookCommand),
    LOOKATBLOCK(() -> getConfig().toggleCommandsConfig.enableLookAtBlockCommand),
    LOOKATENTITY(() -> getConfig().toggleCommandsConfig.enableLookAtEntityCommand),

    MINE(() -> getConfig().toggleCommandsConfig.enableMineCommand),
    STRIPMINE(() -> getConfig().toggleCommandsConfig.enableStripMineCommand),

    MOVE(() -> getConfig().toggleCommandsConfig.enableMoveCommand),

    CLOSESCREEN(() -> getConfig().toggleCommandsConfig.enableCloseScreenCommand),
    MOVEITEM(() -> getConfig().toggleCommandsConfig.enableMoveItemCommand),
    OPENINVENTORY(() -> getConfig().toggleCommandsConfig.enableOpenInventoryCommand),

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
