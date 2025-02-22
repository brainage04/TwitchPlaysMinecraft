package io.github.brainage04.twitchplaysminecraft;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchPlaysMinecraft implements ModInitializer {
	public static final String MOD_NAME = "TwitchPlaysMinecraft";
	public static final String MOD_ID = "twitchplaysminecraft";
	public static final String MOD_ID_SHORT = "tpm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		LOGGER.info("{} initialized (server side).", MOD_NAME);
	}
}