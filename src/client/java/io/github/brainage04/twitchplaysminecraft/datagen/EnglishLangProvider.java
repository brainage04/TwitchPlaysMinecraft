package io.github.brainage04.twitchplaysminecraft.datagen;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.util.LangUtils;
import io.github.brainage04.twitchplaysminecraft.util.StringUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

public class EnglishLangProvider extends FabricLanguageProvider {
    public EnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    private final String prefix = "text.autoconfig.%s.option".formatted(TwitchPlaysMinecraft.MOD_ID);

    private void generatedReflectedTranslations(Class<?> clazz, String baseKey, TranslationBuilder translationBuilder) {
        for (Field field : clazz.getFields()) {
            String newBaseKey = "%s.%s".formatted(baseKey, field.getName());

            translationBuilder.add(newBaseKey, StringUtils.pascalCaseToHumanReadable(field.getName()));

            if (field.getType().isPrimitive()) continue;
            if (field.getType().isEnum()) continue;
            if (field.getType() == String.class) continue;

            generatedReflectedTranslations(field.getType(), newBaseKey, translationBuilder);
        }
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        // config editor
        translationBuilder.add(
                "text.autoconfig.%s.title".formatted(TwitchPlaysMinecraft.MOD_ID),
                "BrainageHUD Config Editor"
        );

        // keybinds
        translationBuilder.add(
                LangUtils.CONFIG_KEYBIND_KEY,
                "Open Config"
        );

        translationBuilder.add(
                LangUtils.GUI_KEYBIND_KEY,
                "Edit GUI"
        );

        // config
        generatedReflectedTranslations(ModConfig.class, prefix, translationBuilder);
    }
}
