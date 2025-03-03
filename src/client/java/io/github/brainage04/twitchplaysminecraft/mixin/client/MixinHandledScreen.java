package io.github.brainage04.twitchplaysminecraft.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class MixinHandledScreen<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @SuppressWarnings("unused")
    protected MixinHandledScreen(Text title) {
        super(title);
    }

    @Shadow @Final protected T handler;

    @Override
    public T getScreenHandler() {
        return this.handler;
    }

    @Shadow protected int x;
    @Shadow protected int y;

    @Inject(at = @At("TAIL"), method = "render")
    private void injected(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.handler.slots.isEmpty()) return;

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 5000);

        for (int i = 0; i < this.handler.slots.size(); i++) {
            Slot slot = this.handler.slots.get(i);

            String index = Integer.toString(i);

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            context.drawTextWithShadow(
                    textRenderer,
                    index,
                    this.x + slot.x,
                    this.y + slot.y,
                    Colors.GREEN
            );
        }

        context.getMatrices().pop();
    }
}
