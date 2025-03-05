package io.github.brainage04.twitchplaysminecraft.util.enums.core;

public interface NamedEnum {
    default String getName() {
        return this.toString().toLowerCase();
    }
}
