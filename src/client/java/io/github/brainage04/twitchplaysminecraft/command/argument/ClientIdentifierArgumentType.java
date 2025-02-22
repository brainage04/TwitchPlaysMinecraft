package io.github.brainage04.twitchplaysminecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.brainage04.twitchplaysminecraft.util.AdvancementUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ClientIdentifierArgumentType implements ArgumentType<Identifier> {
        public static ClientIdentifierArgumentType identifier() {
            return new ClientIdentifierArgumentType();
        }

        public static Identifier getIdentifier(CommandContext<FabricClientCommandSource> context, String name) throws CommandSyntaxException {
            String string = StringArgumentType.getString(context, name);
            try {
                return Identifier.of(string);
            } catch (IllegalArgumentException e) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
            }
        }

        @Override
        public Identifier parse(com.mojang.brigadier.StringReader reader) throws CommandSyntaxException {
            String string = reader.readString();
            try {
                return Identifier.of(string);
            } catch (IllegalArgumentException e) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
            }
        }

        public static CompletableFuture<Suggestions> suggestSelectableAdvancements(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
            MinecraftClient client = context.getSource().getClient();
            if (client.getNetworkHandler() != null) {
                client.getNetworkHandler().getAdvancementHandler()
                    .getManager().getAdvancements()
                        .stream().filter(placedAdvancement -> AdvancementUtils.canSelectAdvancement(context.getSource().getPlayer(), placedAdvancement))
                    .forEach(advancement -> 
                        builder.suggest(advancement.getAdvancementEntry().id().toString())
                    );
            }
            return builder.buildFuture();
        }

        public static CompletableFuture<Suggestions> suggestAllAdvancements(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) {
            MinecraftClient client = context.getSource().getClient();
            if (client.getNetworkHandler() != null) {
                client.getNetworkHandler().getAdvancementHandler()
                    .getManager().getAdvancements()
                    .forEach(advancement ->
                        builder.suggest(advancement.getAdvancementEntry().id().toString())
                    );
            }
            return builder.buildFuture();
        }
}