package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;

import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.getConfig;

public enum ToggleableCommand implements NamedEnum {
    ATTACK(getConfig().toggleCommandsConfig.enableAttackCommand),
    MINE(getConfig().toggleCommandsConfig.enableMineCommand),
    STRIPMINE(getConfig().toggleCommandsConfig.enableStripMineCommand),
    JUMPPLACE(getConfig().toggleCommandsConfig.enableJumpPlaceCommand),
    BRIDGE(getConfig().toggleCommandsConfig.enableBridgeCommand),
    USE(getConfig().toggleCommandsConfig.enableUseCommand),
    DROP(getConfig().toggleCommandsConfig.enableDropCommand),
    DROPHELDITEM(getConfig().toggleCommandsConfig.enableDropHeldItemCommand),
    LOOK(getConfig().toggleCommandsConfig.enableLookCommand),
    LOOKATBLOCK(getConfig().toggleCommandsConfig.enableLookAtBlockCommand),
    LOOKATENTITY(getConfig().toggleCommandsConfig.enableLookAtEntityCommand),
    CRAFT(getConfig().toggleCommandsConfig.enableCraftCommand),
    MOVEITEM(getConfig().toggleCommandsConfig.enableMoveItemCommand),
    OPENINVENTORY(getConfig().toggleCommandsConfig.enableOpenInventoryCommand),
    CLOSESCREEN(getConfig().toggleCommandsConfig.enableCloseScreenCommand),
    HOTBAR(getConfig().toggleCommandsConfig.enableHotbarCommand),
    MOVE(getConfig().toggleCommandsConfig.enableMoveCommand),
    PRESSKEY(getConfig().toggleCommandsConfig.enablePressKeyCommand),
    HOLDKEY(getConfig().toggleCommandsConfig.enableHoldKeyCommand),
    RELEASEKEY(getConfig().toggleCommandsConfig.enableReleaseKeyCommand),
    RELEASEALLKEYS(getConfig().toggleCommandsConfig.enableReleaseAllKeysCommand),
    AVAILABLEGOALS(getConfig().toggleCommandsConfig.enableAvailableGoalsCommand),
    GETGOALINFO(getConfig().toggleCommandsConfig.enableGetGoalInfoCommand),
    GETCURRENTGOAL(getConfig().toggleCommandsConfig.enableGetCurrentGoalCommand),
    SETCURRENTGOAL(getConfig().toggleCommandsConfig.enableSetCurrentGoalCommand),
    CLEARCURRENTGOAL(getConfig().toggleCommandsConfig.enableClearCurrentGoalCommand),
    LOCATESTRUCTURE(getConfig().toggleCommandsConfig.enableLocateStructureCommand),
    REGENERATEAUTHURL(getConfig().toggleCommandsConfig.enableRegenerateAuthUrlCommand),
    COMMANDQUEUE(getConfig().toggleCommandsConfig.enableCommandQueueCommand);

    public final boolean enabled;

    ToggleableCommand(boolean enabled) {
        this.enabled = enabled;
    }
}
