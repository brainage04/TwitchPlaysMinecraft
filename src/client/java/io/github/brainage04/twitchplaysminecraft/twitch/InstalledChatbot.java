package io.github.brainage04.twitchplaysminecraft.twitch;

import com.github.philippheuer.credentialmanager.domain.DeviceAuthorization;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.domain.TwitchScopes;
import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.util.feedback.MessageType;
import io.github.brainage04.twitchplaysminecraft.hud.CommandQueueHud;
import io.github.brainage04.twitchplaysminecraft.util.feedback.ClientFeedbackBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.*;

import static io.github.brainage04.twitchplaysminecraft.util.MapUtils.getMostCommonString;

public class InstalledChatbot {
    private static final List<String> commandQueue = new ArrayList<>();
    private static final Map<String, Integer> argumentCounts = new HashMap<>();
    private static String finalCommand = "";

    public static List<String> getCommandQueue() {
        return commandQueue;
    }

    public static void addToCommandQueue(String command) {
        getCommandQueue().add(command);
        CommandQueueHud.updateLines();
    }

    // todo: figure out why this is causing infinite recursion
    public static void processCommandQueue() {
        for (String command : getCommandQueue()) {
            String[] args = command.substring(finalCommand.length()).split("\\s+", 2);

            // command parsing is finished when arguments are showing up empty
            if (args[0].isEmpty()) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player == null) return;

                FabricClientCommandSource source = ((FabricClientCommandSource) player.networkHandler.getCommandSource());
                new ClientFeedbackBuilder().source(source)
                        .messageType(MessageType.INFO)
                        .text("Attempting to execute command: /%s".formatted(finalCommand))
                        .execute();
                player.networkHandler.sendChatCommand(finalCommand);

                getCommandQueue().clear();

                return;
            }

            argumentCounts.put(args[0], argumentCounts.getOrDefault(args[0], 0) + 1);
        }

        finalCommand += getMostCommonString(argumentCounts);
        argumentCounts.clear();
        processCommandQueue();
    }

    public static void intitialize() {
        // https://gist.github.com/iProdigy/76bc18a8e601243aa021f31fb2a4d121
        Bot bot = new Bot();
        DeviceAuthorization req = bot.getController().startOAuth2DeviceAuthorizationGrantType(
                bot.getIdentityProvider(),
                Arrays.asList(TwitchScopes.CHAT_READ, TwitchScopes.CHAT_EDIT),
                resp -> {
                    OAuth2Credential token = resp.getCredential();
                    if (token != null) {
                        bot.start(token);
                    } else {
                        TwitchPlaysMinecraft.LOGGER.warn("Could not obtain device flow token due to {}", resp.getError());
                    }
                }
        );
        TwitchPlaysMinecraft.LOGGER.info("The user should now visit: {}", req.getCompleteUri());
    }
}
