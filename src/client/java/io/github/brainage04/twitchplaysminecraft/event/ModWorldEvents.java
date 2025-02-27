package io.github.brainage04.twitchplaysminecraft.event;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ModWorldEvents {
    public static void initialize() {
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, client) -> {
            if (client.player == null) return;
            if (InstalledChatbot.getActivationUri().isEmpty()) return;

            new ClientFeedbackBuilder().source(client)
                    .messageType(MessageType.INFO)
                    .text(InstalledChatbot.getAuthText())
                    .execute();
            new ClientFeedbackBuilder().source(client)
                    .messageType(MessageType.INFO)
                    .text(InstalledChatbot.getRegenText())
                    .execute();
        });
    }
}
