package io.github.brainage04.twitchplaysminecraft.command.core;

import io.github.brainage04.twitchplaysminecraft.command.SetGoalCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.registry.RegistryKeys;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ServerModCommands {
    public static void initialize() {
        // voting commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                        literal("setgoal")
                                .then(argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT))
                                        .suggests(ModSuggestionProviders.ADVANCEMENT_SUGGESTIONS)
                                        .executes(context ->
                                                SetGoalCommand.execute(
                                                        context.getSource(),
                                                        RegistryKeyArgumentType.getAdvancementEntry(context, "advancement")
                                                )
                                        )
                                )
                )
        );
    }
}
