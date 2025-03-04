package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;

public enum ImportantStructures implements NamedEnum {
    VILLAGE,
    LAVA_POOL,
    BASTION,
    NETHER_FORTRESS,
    END_PORTAL;

    @Override
    public String getName() {
        return this.toString().toLowerCase();
    }
}
