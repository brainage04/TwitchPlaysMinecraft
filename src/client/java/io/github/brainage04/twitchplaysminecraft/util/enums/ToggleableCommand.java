package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;

import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.getConfig;

public enum ToggleableCommand implements NamedEnum {
    // todo: sort properly
    ATTACK(getConfig().toggleCommandsConfig.enableAttackCommand),
    AVAILABLEGOALS(getConfig().toggleCommandsConfig.enableAvailableGoalsCommand),
    BRIDGE(getConfig().toggleCommandsConfig.enableBridgeCommand),
    CLEARCURRENTGOAL(getConfig().toggleCommandsConfig.enableClearCurrentGoalCommand),
    CLOSESCREEN(getConfig().toggleCommandsConfig.enableCloseScreenCommand),
    COMMANDQUEUE(getConfig().toggleCommandsConfig.enableCommandQueueCommand),
    CRAFT(getConfig().toggleCommandsConfig.enableCraftCommand),
    DROP(getConfig().toggleCommandsConfig.enableDropCommand),
    DROPHELDITEM(getConfig().toggleCommandsConfig.enableDropHeldItemCommand),
    GETCURRENTGOAL(getConfig().toggleCommandsConfig.enableGetCurrentGoalCommand),
    GETGOALINFO(getConfig().toggleCommandsConfig.enableGetGoalInfoCommand),
    HOLDKEY(getConfig().toggleCommandsConfig.enableHoldKeyCommand),
    HOTBAR(getConfig().toggleCommandsConfig.enableHotbarCommand),
    JUMPPLACE(getConfig().toggleCommandsConfig.enableJumpPlaceCommand),
    LOCATESTRUCTURE(getConfig().toggleCommandsConfig.enableLocateStructureCommand),
    LOOK(getConfig().toggleCommandsConfig.enableLookCommand),
    LOOKATBLOCK(getConfig().toggleCommandsConfig.enableLookAtBlockCommand),
    LOOKATENTITY(getConfig().toggleCommandsConfig.enableLookAtEntityCommand),
    MINE(getConfig().toggleCommandsConfig.enableMineCommand),
    MOVE(getConfig().toggleCommandsConfig.enableMoveCommand),
    MOVEITEM(getConfig().toggleCommandsConfig.enableMoveItemCommand),
    OPENINVENTORY(getConfig().toggleCommandsConfig.enableOpenInventoryCommand),
    PRESSKEY(getConfig().toggleCommandsConfig.enablePressKeyCommand),
    REGENERATEAUTHURL(getConfig().toggleCommandsConfig.enableRegenerateAuthUrlCommand),
    RELEASEALLKEYS(getConfig().toggleCommandsConfig.enableReleaseAllKeysCommand),
    RELEASEKEY(getConfig().toggleCommandsConfig.enableReleaseKeyCommand),
    SETCURRENTGOAL(getConfig().toggleCommandsConfig.enableSetCurrentGoalCommand),
    STRIPMINE(getConfig().toggleCommandsConfig.enableStripMineCommand),
    USE(getConfig().toggleCommandsConfig.enableUseCommand);

    public final boolean enabled;

    ToggleableCommand(boolean enabled) {
        this.enabled = enabled;
    }
}
