package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;

public enum CardinalDirection implements NamedEnum {
    NORTH(-180),
    NORTHEAST(-135),
    EAST(-90),
    SOUTHEAST(-45),
    SOUTH(0),
    SOUTHWEST(45),
    WEST(90),
    NORTHWEST(135);

    public final int yaw;

    CardinalDirection(int yaw) {
        this.yaw = yaw;
    }

    public boolean isDiagonal() {
        return this.ordinal() % 2 == 1;
    }
}
