package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class RegistryUtils {
    public static String getKeyName(RegistryKey<?> key, boolean firstCapitalOnly) {
        return StringUtils.snakeCaseToPascalCase(key.getValue().getPath(), firstCapitalOnly);
    }

    public static String getEntryName(RegistryEntry<?> entry, boolean firstCapitalOnly) {
        return StringUtils.snakeCaseToPascalCase(Identifier.of(entry.getIdAsString()).getPath(), firstCapitalOnly);
    }
}
