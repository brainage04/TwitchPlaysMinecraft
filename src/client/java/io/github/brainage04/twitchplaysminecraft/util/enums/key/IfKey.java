package io.github.brainage04.twitchplaysminecraft.util.enums.key;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Function;

public enum IfKey implements NamedEnum {
    JUMP(options -> options.jumpKey),
    ATTACK(options -> options.attackKey);

    public final Function<GameOptions, KeyBinding> function;

    IfKey(Function<GameOptions, KeyBinding> function) {
        this.function = function;
    }
}
