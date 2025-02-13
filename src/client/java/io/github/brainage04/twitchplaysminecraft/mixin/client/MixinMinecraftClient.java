package io.github.brainage04.twitchplaysminecraft.mixin.client;

import io.github.brainage04.twitchplaysminecraft.util.CommandUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Shadow @Nullable public Screen currentScreen;

    @Inject(at = @At("TAIL"), method = "setScreen")
    private void injected(Screen screen, CallbackInfo ci) {
        if (this.currentScreen instanceof ChatScreen) return;
        CommandUtils.interruptCurrentInteractionThread();
    }
}
