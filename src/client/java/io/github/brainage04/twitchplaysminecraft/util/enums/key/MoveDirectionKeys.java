package io.github.brainage04.twitchplaysminecraft.util.enums.key;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Function;

public enum MoveDirectionKeys implements NamedEnum {
    FORWARD(options -> options.forwardKey),
    BACK(options -> options.backKey),
    LEFT(options -> options.leftKey),
    RIGHT(options -> options.rightKey);

    public final Function<GameOptions, KeyBinding> function;

    MoveDirectionKeys(Function<GameOptions, KeyBinding> function) {
        this.function = function;
    }
}
