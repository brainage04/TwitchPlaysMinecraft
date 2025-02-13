package io.github.brainage04.twitchplaysminecraft.mixin.client;

import io.github.brainage04.twitchplaysminecraft.util.CommandUtils;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MixinScreen {
    @Inject(at = @At("TAIL"), method = "close")
    private void injected(CallbackInfo ci) {
        CommandUtils.interruptCurrentInteractionThread();
    }
}
