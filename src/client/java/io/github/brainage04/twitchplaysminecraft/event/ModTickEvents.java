package io.github.brainage04.twitchplaysminecraft.event;

import io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.getConfig;

public class ModTickEvents {
    private static int ticks = 0;

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (!getConfig().commandQueueConfig.enabled) return;

            ticks++;
            if (ticks % 100 != 0) return;
            if (InstalledChatbot.getCommandQueue().isEmpty()) {
                ticks = 0;
                return;
            }

            InstalledChatbot.processCommandQueue(client);
            ticks = 0;
        });
    }
}
