package io.github.brainage04.twitchplaysminecraft.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeBookScreen.class)
public class MixinRecipeBookScreen {
    @Shadow @Final public RecipeBookWidget<?> recipeBook;

    @Inject(at = @At("TAIL"), method = "render")
    public void injected(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!this.recipeBook.isOpen()) return;

        List<? extends ClickableWidget> buttons = this.recipeBook.recipesArea.alternatesWidget.isVisible()
                ? this.recipeBook.recipesArea.alternatesWidget.alternativeButtons
                : this.recipeBook.recipesArea.resultButtons.stream().filter(button -> button.visible).toList();

        if (buttons.isEmpty()) return;

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 5000);

        for (int i = 0; i < buttons.size(); i++) {
            ClickableWidget button = buttons.get(i);

            String index = Integer.toString(i);

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            context.drawTextWithShadow(
                    textRenderer,
                    index,
                    button.getX() + 4,
                    button.getY() + 4,
                    Colors.GREEN
            );
        }

        context.getMatrices().pop();
    }
}
