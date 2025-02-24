package io.github.brainage04.twitchplaysminecraft.util;

import io.github.brainage04.twitchplaysminecraft.hud.AdvancementTrackingHud;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdvancementUtils {
    private static PlacedAdvancement currentAdvancement = null;

    public static PlacedAdvancement getCurrentAdvancement() {
        return currentAdvancement;
    }

    public static void setCurrentAdvancement(PlacedAdvancement currentAdvancement) {
        AdvancementUtils.currentAdvancement = currentAdvancement;
        AdvancementTrackingHud.updateLines();
    }

    public static Text getAdvancementName(PlacedAdvancement placedAdvancement) {
        if (placedAdvancement.getAdvancement().name().isPresent()) {
            return placedAdvancement.getAdvancement().name().get();
        } else if (placedAdvancement.getAdvancement().display().isPresent()) {
            return placedAdvancement.getAdvancement().display().get().getTitle();
        }

        return Text.literal(AdvancementUtils.getCurrentAdvancement().getAdvancementEntry().id().toString());
    }

    public static Text getAdvancementDescription(PlacedAdvancement placedAdvancement) {
        if (placedAdvancement.getAdvancement().display().isPresent()) {
            AdvancementDisplay display = placedAdvancement.getAdvancement().display().get();
            return display.getDescription();
        }

        return null;
    }

    public static PlacedAdvancement getAdvancementById(Identifier id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) return null;

        return client.getNetworkHandler().getAdvancementHandler().getManager().get(id);
    }

    public static List<PlacedAdvancement> getAvailableAdvancements() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || client.getNetworkHandler() == null) return List.of();

        return client.getNetworkHandler().getAdvancementHandler().getManager().getAdvancements().stream()
                .filter(advancement -> canSelectAdvancement(player, advancement))
                .collect(Collectors.toList());
    }

    public static boolean canSelectAdvancement(ClientPlayerEntity player, PlacedAdvancement placedAdvancement) {
        Map<AdvancementEntry, AdvancementProgress> advancementProgresses = player.networkHandler.getAdvancementHandler().advancementProgresses;

        // check if advancement is already complete
        if (advancementProgresses.get(placedAdvancement.getAdvancementEntry()).isDone()) return false;
        // if advancement has no parent then there are no prerequisites
        PlacedAdvancement parent = placedAdvancement.getParent();
        if (parent == null) return true;
        // only the parent's progress needs to be checked (it is assumed that all previous advancements before the parent are done)
        return advancementProgresses.get(parent.getAdvancementEntry()).isDone();
    }
}
