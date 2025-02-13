package io.github.brainage04.twitchplaysminecraft.event;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ModWorldEvents {
    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, client) -> {

        });
    }
}
