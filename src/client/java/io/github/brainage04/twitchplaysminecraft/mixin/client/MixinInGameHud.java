package io.github.brainage04.twitchplaysminecraft.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Shadow @Final private MinecraftClient client;

    @Unique
    private void renderHotbarSlotOverlay(DrawContext context, int i, int x, int y) {
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 5000);

        String index = Integer.toString(i);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        context.drawTextWithShadow(
                textRenderer,
                index,
                x,
                y,
                Colors.GREEN
        );

        context.getMatrices().pop();
    }

    @Inject(method = "renderHotbar", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
            shift = At.Shift.AFTER,
            ordinal = 0
    ))
    private void injected(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (this.client.currentScreen != null) {
            if (!(this.client.currentScreen instanceof ChatScreen)) return;
        }

        int i = context.getScaledWindowWidth() / 2;
        for (int m = 0; m < 9; m++) {
            int n = i - 90 + m * 20 + 2;
            int o = context.getScaledWindowHeight() - 16 - 3;
            renderHotbarSlotOverlay(context, m, n, o);
        }
    }
}
