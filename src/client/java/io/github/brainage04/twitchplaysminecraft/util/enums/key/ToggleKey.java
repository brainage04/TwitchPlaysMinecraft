package io.github.brainage04.twitchplaysminecraft.util.enums.key;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Function;

public enum ToggleKey implements NamedEnum {
    FORWARD(options -> options.forwardKey),
    BACK(options -> options.backKey),
    LEFT(options -> options.leftKey),
    RIGHT(options -> options.rightKey),
    SPRINT(options -> options.sprintKey),
    SNEAK(options -> options.sneakKey),
    JUMP(options -> options.jumpKey),
    ATTACK(options -> options.attackKey, new String[]{"leftclick"}),
    USE(options -> options.useKey, new String[]{"rightclick"});

    public final Function<GameOptions, KeyBinding> function;
    public final String[] otherNames;

    ToggleKey(Function<GameOptions, KeyBinding> function) {
        this.function = function;
        this.otherNames = new String[0];
    }

    ToggleKey(Function<GameOptions, KeyBinding> function, String[] otherNames) {
        this.function = function;
        this.otherNames = otherNames;
    }
}
