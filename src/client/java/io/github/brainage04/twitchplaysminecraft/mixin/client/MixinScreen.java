package io.github.brainage04.twitchplaysminecraft.mixin.client;

import io.github.brainage04.twitchplaysminecraft.command.admin.StopItCommand;
import io.github.brainage04.twitchplaysminecraft.command.key.ToggleKeyCommands;
import io.github.brainage04.twitchplaysminecraft.util.CommandUtils;
import io.github.brainage04.twitchplaysminecraft.util.SourceUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.brainage04.twitchplaysminecraft.util.ConfigUtils.getConfig;

@Mixin(Screen.class)
public class MixinScreen {
    @Shadow @Nullable protected MinecraftClient client;

    @Inject(at = @At("TAIL"), method = "close")
    private void injected(CallbackInfo ci) {
        if (client != null && getConfig().cancelAllCommandsOnScreenOpen) {
            StopItCommand.execute(SourceUtils.getSource(this.client));
        }
        ToggleKeyCommands.resetTicks();
        CommandUtils.interruptCurrentInteractionThread();
    }
}
