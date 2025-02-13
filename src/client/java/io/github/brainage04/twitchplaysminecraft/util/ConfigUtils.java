
package io.github.brainage04.twitchplaysminecraft.util;

import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class ConfigUtils {
    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static void saveConfig() {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }

    public static void loadConfig() {
        AutoConfig.getConfigHolder(ModConfig.class).load();
    }
}