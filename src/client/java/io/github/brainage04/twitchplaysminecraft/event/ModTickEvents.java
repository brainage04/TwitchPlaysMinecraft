package io.github.brainage04.twitchplaysminecraft.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ModTickEvents {
    private static int ticks = 0;

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            ticks++;

            if (ticks % 100 != 0) return;

            //InstalledChatbot.processCommandQueue();
        });
    }
}
