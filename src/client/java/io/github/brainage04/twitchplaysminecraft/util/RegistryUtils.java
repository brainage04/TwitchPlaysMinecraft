package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class RegistryUtils {
    public static String getKeyName(RegistryKey<?> key) {
        return StringUtils.snakeCaseToHumanReadable(key.getValue().getPath(), true, false);
    }

    public static String getEntryName(RegistryEntry<?> entry) {
        return StringUtils.snakeCaseToHumanReadable(Identifier.of(entry.getIdAsString()).getPath(), true, false);
    }
}
