package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;

public enum ActionType implements NamedEnum {
    MOVE,
    SWAP,
    QUICKMOVE;

    @Override
    public String getName() {
        return this.toString().toLowerCase();
    }
}
