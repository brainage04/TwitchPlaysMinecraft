package io.github.brainage04.twitchplaysminecraft.util.enums.key;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Function;

public enum SinglePressKey implements NamedEnum {
    JUMP(options -> options.jumpKey),
    INVENTORY(options -> options.inventoryKey, new String[]{"openinventory"}),
    SWAPHANDS(options -> options.swapHandsKey),
    ATTACK(options -> options.attackKey),
    PICKITEM(options -> options.pickItemKey, new String[]{"pickblock"});

    public final Function<GameOptions, KeyBinding> function;
    public final String[] otherNames;

    SinglePressKey(Function<GameOptions, KeyBinding> function) {
        this.function = function;
        this.otherNames = new String[0];
    }

    SinglePressKey(Function<GameOptions, KeyBinding> function, String[] otherNames) {
        this.function = function;
        this.otherNames = otherNames;
    }
}
