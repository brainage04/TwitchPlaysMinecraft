package io.github.brainage04.twitchplaysminecraft;

import io.github.brainage04.twitchplaysminecraft.command.core.ClientCommands;
import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.event.ModTickEvents;
import io.github.brainage04.twitchplaysminecraft.event.ModWorldEvents;
import io.github.brainage04.twitchplaysminecraft.hud.core.HUDRenderer;
import io.github.brainage04.twitchplaysminecraft.keybind.ModKeys;
import io.github.brainage04.twitchplaysminecraft.twitch.InstalledChatbot;
import io.github.brainage04.twitchplaysminecraft.util.RunnableScheduler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import static io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft.*;

public class TwitchPlaysMinecraftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientCommands.initialize();
		ModKeys.registerKeys();
		ModTickEvents.initialize();
		ModWorldEvents.initialize();

		InstalledChatbot.intitialize();

		RunnableScheduler.initialize();

		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

		HudRenderCallback.EVENT.register(new HUDRenderer());

		LOGGER.info("{} initialized (client side).", MOD_NAME);
	}
}