package io.github.brainage04.twitchplaysminecraft.twitch;

import com.github.philippheuer.credentialmanager.domain.DeviceAuthorization;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.domain.TwitchScopes;
import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.hud.CommandQueueHud;
import io.github.brainage04.twitchplaysminecraft.util.CommandUtils;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.*;

public class InstalledChatbot {
    private static Bot bot;
    private static final List<String> commandQueue = new ArrayList<>();
    private static String activationUri = "";

    public static Bot getBot() {
        return bot;
    }

    public static List<String> getCommandQueue() {
        return commandQueue;
    }

    public static void addToCommandQueue(String command) {
        getCommandQueue().add(command);
        CommandQueueHud.updateLines();
    }

    public static void clearCommandQueue() {
        getCommandQueue().clear();
        CommandQueueHud.updateLines();
    }

    public static String getActivationUri() {
        return activationUri;
    }

    public static MutableText getAuthText() {
        return Text.literal("In order for viewers to receive command feedback via Twitch chat, you must authorise TPM Bot to send messages on your behalf by clicking ")
                .append(Text.literal("here")
                        .setStyle(
                                Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                                InstalledChatbot.getActivationUri()))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                Text.literal("Opens the URL to authorise TPM Bot in your default browser when clicked."))))
                        .formatted(Formatting.UNDERLINE))
                .append(".");
    }

    public static MutableText getRegenText() {
        return Text.literal("\"")
                .append(Text.literal("INCORRECT CODE!").formatted(Formatting.RED))
                .append("\"? Request a new one by clicking ")
                .append(Text.literal("here")
                        .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/regenerateauthurl"))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Text.literal("Runs \"/regenerateauthurl\" when clicked."))))
                        .formatted(Formatting.UNDERLINE))
                .append(" or with \"/regenerateauthurl\".");
    }

    public static void intitialize() {
        // https://gist.github.com/iProdigy/76bc18a8e601243aa021f31fb2a4d121
        bot = new Bot();
        DeviceAuthorization req = getBot().getController().startOAuth2DeviceAuthorizationGrantType(
                getBot().getIdentityProvider(),
                Arrays.asList(TwitchScopes.CHAT_READ, TwitchScopes.CHAT_EDIT),
                resp -> {
                    OAuth2Credential token = resp.getCredential();
                    if (token != null) {
                        getBot().start(token);

                        MinecraftClient client = MinecraftClient.getInstance();
                        if (client == null) return;
                        new ClientFeedbackBuilder().source(client)
                                .messageType(MessageType.SUCCESS)
                                .text("TPM Bot has now been authorized.")
                                .execute();
                        InstalledChatbot.activationUri = "";
                    } else {
                        TwitchPlaysMinecraft.LOGGER.warn("Could not obtain device flow token due to {}", resp.getError());
                    }
                }
        );
        activationUri = req.getCompleteUri();
        TwitchPlaysMinecraft.LOGGER.info("The user should now visit: {}", getActivationUri());
    }

    public static void regenerate() {
        intitialize();

        if (MinecraftClient.getInstance().player == null) return;

        new ClientFeedbackBuilder().source(MinecraftClient.getInstance())
                .messageType(MessageType.INFO)
                .text(getAuthText())
                .execute();
        new ClientFeedbackBuilder().source(MinecraftClient.getInstance())
                .messageType(MessageType.INFO)
                .text(getRegenText())
                .execute();
    }

    public static void processCommandQueue(MinecraftClient client) {
        String command = CommandUtils.getMostPopularCommand(getCommandQueue());
        new ClientFeedbackBuilder().source(client)
                .messageType(MessageType.INFO)
                .text("Most popular command: %s".formatted(command));

        if (client.getNetworkHandler() == null) return;
        client.getNetworkHandler().sendChatCommand(command);

        clearCommandQueue();
    }
}
