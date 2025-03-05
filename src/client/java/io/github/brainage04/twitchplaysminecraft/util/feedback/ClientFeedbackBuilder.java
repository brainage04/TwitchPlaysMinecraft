package io.github.brainage04.twitchplaysminecraft.util.feedback;

import io.github.brainage04.twitchplaysminecraft.command.util.feedback.FeedbackBuilder;
import io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public class ClientFeedbackBuilder extends FeedbackBuilder<FabricClientCommandSource> {
    public ClientFeedbackBuilder() {
        source = SourceUtils.getSource();
    }

    @Override
    protected void sendFeedback() {
        if (source != null) {
            source.sendFeedback(text);

            if (source.getClient().player == null) return;
            source.getClient().player.playSound(soundEvent, volume, pitch);
        }

        if (sendInTwitchChat) {
            InstalledChatbot.getBot().sendChatMessage(text.getString());
        }
    }

    // todo: i don't think i actually need this
    public FeedbackBuilder<FabricClientCommandSource> source(FabricClientCommandSource source) {
        this.source = source;
        return this;
    }

    // todo: get rid of this
    public FeedbackBuilder<FabricClientCommandSource> source(MinecraftClient client) {
        source = SourceUtils.getSource(client);
        return this;
    }

    public FeedbackBuilder<FabricClientCommandSource> source(ClientPlayerEntity player) {
        source = SourceUtils.getSource(player);
        return this;
    }

    public FeedbackBuilder<FabricClientCommandSource> source(ClientPlayNetworkHandler networkHandler) {
        source = SourceUtils.getSource(networkHandler);
        return this;
    }

}
