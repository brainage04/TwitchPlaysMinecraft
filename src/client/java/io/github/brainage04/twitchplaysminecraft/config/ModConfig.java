package io.github.brainage04.twitchplaysminecraft.config;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@SuppressWarnings("CanBeFinal")
@Config(name = TwitchPlaysMinecraft.MOD_ID)
public class ModConfig implements ConfigData {
    public boolean textShadows = true;
    @ConfigEntry.ColorPicker public int primaryTextColour = 0xffffff;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 255) public int backdropOpacity = 0;

    public int elementPadding = 2;
    public int screenMargin = 5;

    @ConfigEntry.Gui.CollapsibleObject public ToggleCommandsConfig toggleCommandsConfig = new ToggleCommandsConfig();
    @ConfigEntry.Gui.CollapsibleObject public CommandQueueConfig commandQueueConfig = new CommandQueueConfig();
    @ConfigEntry.Gui.CollapsibleObject public GoalConfig goalConfig = new GoalConfig();

    public static class ToggleCommandsConfig {
        public boolean enableTogglingCommands = true;
        public boolean disableAllCommands = false;

        public boolean enableCommandQueueCommand = true;
        public boolean enableRegenerateAuthUrlCommand = true;
        public boolean enableStopItCommand = true;

        public boolean enableAttackCommand = true;

        public boolean enableCraftCommand = true;

        public boolean enableDropCommand = true;
        public boolean enableDropHeldItemCommand = true;

        public boolean enableClearCurrentGoalCommand = true;
        public boolean enableGetCurrentGoalCommand = true;
        public boolean enableGetGoalCommand = true;
        public boolean enableSelectableGoalsCommand = true;
        public boolean enableSelectCurrentGoalCommand = true;

        public boolean enablePressKeyCommand = true;
        public boolean enableHoldKeyCommand = true;
        public boolean enableReleaseKeyCommand = true;
        public boolean enableReleaseAllKeysCommand = true;

        public boolean enableLookCommand = true;
        public boolean enableLookAtBlockCommand = true;
        public boolean enableLookAtEntityCommand = true;

        public boolean enableMineCommand = true;
        public boolean enableStripMineCommand = true;

        public boolean enableMoveCommand = true;

        public boolean enableCloseScreenCommand = true;
        public boolean enableMoveItemCommand = true;
        public boolean enableOpenInventoryCommand = true;

        public boolean enableBridgeCommand = true;
        public boolean enableJumpPlaceCommand = true;
        public boolean enableUseCommand = true;

        public boolean enableHotbarCommand = true;
        public boolean enableLocateStructureCommand = true;
    }

    public static class CommandQueueConfig {
        public boolean enabled = true;
        public int intervalInSeconds = 5;
        @ConfigEntry.Gui.CollapsibleObject public CoreSettings coreSettings;

        public CommandQueueConfig() {
            this.coreSettings = new CoreSettings(0, "Command Queue HUD", true, 5, 5, ElementAnchor.TOPLEFT, false, 100);
        }
    }

    public static class GoalConfig {
        public boolean displayRecipeGoals = false;
        @ConfigEntry.Gui.CollapsibleObject public CoreSettings coreSettings;

        public GoalConfig() {
            this.coreSettings = new CoreSettings(1, "Goal HUD", true, -5, 5, ElementAnchor.TOPRIGHT, false, 100);
        }
    }

    public static class CoreSettings {
        @ConfigEntry.Gui.Excluded public int elementId;
        @ConfigEntry.Gui.Excluded public String elementName;

        public boolean displayEnabled;
        public int x;
        public int y;
        public ElementAnchor elementAnchor;

        public boolean overrideGlobalBackdropOpacity;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 255) public int backdropOpacity;

        public CoreSettings(int elementId, String elementName, boolean displayEnabled, int x, int y, ElementAnchor elementAnchor, boolean overrideGlobalBackdropOpacity, int backdropOpacity) {
            this.elementId = elementId;
            this.elementName = elementName;

            this.displayEnabled = displayEnabled;
            this.x = x;
            this.y = y;
            this.elementAnchor = elementAnchor;

            this.overrideGlobalBackdropOpacity = overrideGlobalBackdropOpacity;
            this.backdropOpacity = backdropOpacity;
        }
    }

    public enum ElementAnchor {
        TOPLEFT("Top Left"),
        TOP("Top"),
        TOPRIGHT("Top Right"),
        LEFT("Left"),
        CENTER("Center"),
        RIGHT("Right"),
        BOTTOMLEFT("Bottom Left"),
        BOTTOM("Bottom"),
        BOTTOMRIGHT("Bottom Right");

        private final String name;

        ElementAnchor(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}