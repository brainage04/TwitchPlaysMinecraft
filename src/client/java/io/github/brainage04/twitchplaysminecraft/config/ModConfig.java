package io.github.brainage04.twitchplaysminecraft.config;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

// todo: check if CanBeFinal is needed (check if making fields final fucks anything up)
@SuppressWarnings({"unused", "CanBeFinal"})
@Config(name = TwitchPlaysMinecraft.MOD_ID)
public class ModConfig implements ConfigData {
    public boolean textShadows = true;
    @ConfigEntry.ColorPicker public int primaryTextColour = 0xffffff;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 255) public int backdropOpacity = 0;

    public int elementPadding = 2;
    public int screenMargin = 5;

    @ConfigEntry.Gui.CollapsibleObject public CommandQueueConfig commandQueueConfig = new CommandQueueConfig();
    @ConfigEntry.Gui.CollapsibleObject public AdvancementTrackingConfig advancementTrackingConfig = new AdvancementTrackingConfig();

    @SuppressWarnings("CanBeFinal")
    public static class CommandQueueConfig {
        public boolean enabled;
        @ConfigEntry.Gui.CollapsibleObject public CoreSettings coreSettings;

        public CommandQueueConfig() {
            this.enabled = true;
            this.coreSettings = new CoreSettings(0, "Command Queue HUD", true, 5, 5, ElementAnchor.TOPLEFT, false, 100);
        }
    }

    public static class AdvancementTrackingConfig {
        @ConfigEntry.Gui.CollapsibleObject public CoreSettings coreSettings;

        public AdvancementTrackingConfig() {
            this.coreSettings = new CoreSettings(1, "Advancement Tracking HUD", true, -5, 5, ElementAnchor.TOPRIGHT, false, 100);
        }
    }

    @SuppressWarnings("CanBeFinal")
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

    public enum ClicksPerSecondFormat {
        NONE("None"),
        LEFT_CLICK("Left Click"),
        RIGHT_CLICK("Right Click"),
        BOTH("Both");

        private final String name;

        ClicksPerSecondFormat(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum DurabilityFormat {
        NONE("None"),
        ONE_NUMBER("One Number"),
        BOTH_NUMBERS("Both Numbers"),
        PERCENTAGE("Percentage");

        private final String name;

        DurabilityFormat(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum KillDeathRatioFormat {
        BOTH_NUMBERS("Both Numbers"),
        SIMPLIFIED("Simplified");

        private final String name;

        KillDeathRatioFormat(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}