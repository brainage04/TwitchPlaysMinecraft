package io.github.brainage04.twitchplaysminecraft.util;

import io.github.brainage04.twitchplaysminecraft.hud.GoalHud;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// todo: needs refactoring (shouldn't be refercing MinecraftClient standalone etc)
public class AdvancementUtils {
    private static PlacedAdvancement currentAdvancement = null;

    public static PlacedAdvancement getCurrentAdvancement() {
        return currentAdvancement;
    }

    public static void setCurrentAdvancement(PlacedAdvancement currentAdvancement) {
        AdvancementUtils.currentAdvancement = currentAdvancement;
        GoalHud.updateLines();
    }

    public static Text getAdvancementName(PlacedAdvancement placedAdvancement) {
        if (placedAdvancement.getAdvancement().name().isPresent()) {
            return placedAdvancement.getAdvancement().name().get();
        } else if (placedAdvancement.getAdvancement().display().isPresent()) {
            return placedAdvancement.getAdvancement().display().get().getTitle();
        }

        return Text.literal(placedAdvancement.getAdvancementEntry().id().toString());
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

    public static List<PlacedAdvancement> getSelectableAdvancements() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || client.getNetworkHandler() == null) return List.of();

        return client.getNetworkHandler().getAdvancementHandler().getManager().getAdvancements().stream()
                .filter(advancement -> canSelectAdvancement(player, advancement))
                .collect(Collectors.toList());
    }

    public static List<PlacedAdvancement> getVisibleAdvancements() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || client.getNetworkHandler() == null) return List.of();

        return client.getNetworkHandler().getAdvancementHandler().getManager().getAdvancements().stream().toList();
    }

    // todo: update when advancement is done using mixin
    public static List<PlacedAdvancement> getAchievedAdvancements() {
        if (MinecraftClient.getInstance().player == null) return List.of();
        Map<AdvancementEntry, AdvancementProgress> advancementProgresses = MinecraftClient.getInstance().player.networkHandler.getAdvancementHandler().advancementProgresses;

        List<PlacedAdvancement> advancements = new ArrayList<>();

        for (PlacedAdvancement placedAdvancement : getVisibleAdvancements()) {
            if (advancementProgresses.get(placedAdvancement.getAdvancementEntry()).isDone()) {
                advancements.add(placedAdvancement);
            }
        }

        return advancements;
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
