package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Function;

public enum MoveDirection implements NamedEnum {
    FORWARD(options -> options.forwardKey),
    BACK(options -> options.backKey),
    LEFT(options -> options.leftKey),
    RIGHT(options -> options.rightKey);

    public final Function<GameOptions, KeyBinding> function;

    MoveDirection(Function<GameOptions, KeyBinding> function) {
        this.function = function;
    }
}
