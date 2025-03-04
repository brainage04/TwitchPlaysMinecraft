package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class RegistryUtils {
    public static <T> Registry<T> getRegistry(RegistryKey<Registry<T>> key, ClientWorld world) {
        return world.getRegistryManager().getOptional(key).orElse(null);
    }

    public static String getKeyName(RegistryKey<?> key) {
        return StringUtils.snakeCaseToHumanReadable(key.getValue().getPath(), true, false);
    }

    public static String getEntryName(RegistryEntry<?> entry) {
        return StringUtils.snakeCaseToHumanReadable(Identifier.of(entry.getIdAsString()).getPath(), true, false);
    }
}
