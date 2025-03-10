package io.github.brainage04.twitchplaysminecraft.util.enums.key;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Function;

public enum WhileKey implements NamedEnum {
    INVENTORY(options -> options.inventoryKey),
    OPENINVENTORY(options -> options.inventoryKey),
    SWAPHANDS(options -> options.swapHandsKey),
    PICKITEM(options -> options.pickItemKey),
    PICKBLOCK(options -> options.pickItemKey);

    public final Function<GameOptions, KeyBinding> function;

    WhileKey(Function<GameOptions, KeyBinding> function) {
        this.function = function;
    }
}
