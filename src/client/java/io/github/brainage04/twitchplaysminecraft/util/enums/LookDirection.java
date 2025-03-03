package io.github.brainage04.twitchplaysminecraft.util.enums;

import io.github.brainage04.twitchplaysminecraft.util.enums.core.NamedEnum;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.function.BiConsumer;

public enum LookDirection implements NamedEnum {
    UP((player, degrees) -> player.setPitch(player.getPitch() - degrees)),
    DOWN((player, degrees) -> player.setPitch(player.getPitch() + degrees)),
    LEFT((player, degrees) -> player.setYaw(player.getYaw() - degrees)),
    RIGHT((player, degrees) -> player.setYaw(player.getYaw() + degrees));

    public final BiConsumer<ClientPlayerEntity, Integer> consumer;

    LookDirection(BiConsumer<ClientPlayerEntity, Integer> consumer) {
        this.consumer = consumer;
    }

    @Override
    public String getName() {
        return this.toString().toLowerCase();
    }
}
