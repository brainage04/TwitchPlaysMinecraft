package io.github.brainage04.twitchplaysminecraft.keybind;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.hud.core.HUDElementEditor;
import io.github.brainage04.twitchplaysminecraft.util.LangUtils;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeys {
    public static final KeyBinding configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            LangUtils.CONFIG_KEYBIND_KEY,
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_ENTER,
            TwitchPlaysMinecraft.MOD_NAME
    ));

    public static final KeyBinding guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            LangUtils.GUI_KEYBIND_KEY,
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_ADD,
            TwitchPlaysMinecraft.MOD_NAME
    ));

    public static void registerKeys() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (configKey.isPressed()) {
                MinecraftClient.getInstance().setScreen(
                        AutoConfig.getConfigScreen(ModConfig.class, MinecraftClient.getInstance().currentScreen).get()
                );
            }

            if (guiKey.isPressed()) {
                MinecraftClient.getInstance().setScreen(
                        new HUDElementEditor()
                );
            }
        });
    }
}
