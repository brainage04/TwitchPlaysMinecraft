package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;

public enum LookStraightDirection implements NamedEnum {
    UP(0, -90),
    DOWN(0, 90),
    LEFT(-90, 0),
    RIGHT(90, 0),
    AHEAD(0, 0),
    BEHIND(180, 0);

    public final int yaw;
    public final int pitch;

    LookStraightDirection(int yaw, int pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
