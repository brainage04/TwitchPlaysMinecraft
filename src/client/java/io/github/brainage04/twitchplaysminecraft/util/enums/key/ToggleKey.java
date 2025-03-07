package io.github.brainage04.twitchplaysminecraft.util.enums.key;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Function;

public enum ToggleKey implements NamedEnum {
    SPRINT(options -> options.sprintKey),
    SNEAK(options -> options.sneakKey);

    public final Function<GameOptions, KeyBinding> function;

    ToggleKey(Function<GameOptions, KeyBinding> function) {
        this.function = function;
    }
}
