package io.github.brainage04.twitchplaysminecraft.command.core;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import io.github.brainage04.twitchplaysminecraft.command.*;
import io.github.brainage04.twitchplaysminecraft.config.ModConfig;
import io.github.brainage04.twitchplaysminecraft.hud.core.HUDElementEditor;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClientCommands {
    public static void initialize() {
        // config commands
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal(TwitchPlaysMinecraft.MOD_ID + "config")
                        .executes(context -> {
                            MinecraftClient.getInstance().send(() -> context.getSource().getClient().setScreen(
                                    AutoConfig.getConfigScreen(ModConfig.class, context.getSource().getClient().currentScreen).get()
                            ));
                            return 1;
                        })
                )
        ));

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal(TwitchPlaysMinecraft.MOD_ID + "gui")
                        .executes(context -> {
                            MinecraftClient.getInstance().send(() -> context.getSource().getClient().setScreen(
                                    new HUDElementEditor()
                            ));
                            return 1;
                        })
                )
        ));

        // world interaction commands
        AttackCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("attack")
                                .executes(context ->
                                        AttackCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );

        MineCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("mine")
                                .then(argument("block", StringArgumentType.string())
                                        .executes(context ->
                                                MineCommand.execute(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "block"),
                                                        1
                                                )
                                        )
                                        .then(argument("count", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        MineCommand.execute(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "block"),
                                                                IntegerArgumentType.getInteger(context, "count")
                                                        )
                                                )
                                        )
                                )
                )
        );
        MineTreeCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("minetree")
                                .executes(context ->
                                        MineTreeCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );
        StripMineCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("stripmine")
                                .then(argument("count", IntegerArgumentType.integer())
                                        .executes(context ->
                                                StripMineCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "count")
                                                )
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("jumpplace")
                                .then(argument("count", IntegerArgumentType.integer())
                                        .executes(context ->
                                                JumpPlaceCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "count")
                                                )
                                        )
                                )
                )
        );

        UseCommand.initialize();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("use")
                                .then(argument("count", IntegerArgumentType.integer())
                                        .executes(context ->
                                                UseCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "count")
                                                )
                                        )
                                )
                )
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("drop")
                                .then(argument("slot", IntegerArgumentType.integer())
                                        .then(argument("count", IntegerArgumentType.integer(1, 64))
                                                .executes(context ->
                                                        DropCommand.execute(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "slot"),
                                                                IntegerArgumentType.getInteger(context, "count")
                                                        )
                                                )
                                        )
                                        .then(literal("all")
                                                .executes(context ->
                                                        DropCommand.executeDropAll(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "slot")
                                                        )
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("drophelditem")
                                .then(argument("count", IntegerArgumentType.integer())
                                        .executes(context ->
                                                DropCommand.execute(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "count")
                                                )
                                        )
                                )
                                .then(literal("all")
                                        .executes(context ->
                                                DropCommand.executeDropAll(
                                                        context.getSource()
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("look")
                                .then(argument("direction", StringArgumentType.string())
                                        .suggests(ClientSuggestionProviders.LOOK_DIRECTION_SUGGESTIONS)
                                        .then(argument("degrees", IntegerArgumentType.integer(1))
                                                .executes(context ->
                                                        LookCommand.execute(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "direction"),
                                                                IntegerArgumentType.getInteger(context, "degrees")
                                                        )
                                                )
                                        )
                                )
                )
        );

        // screen commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("craft")
                                .then(literal("item")
                                        .then(argument("item", StringArgumentType.string())
                                                .executes(context ->
                                                        CraftCommand.execute(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "item"),
                                                                1
                                                        )
                                                )
                                                .then(argument("count", IntegerArgumentType.integer(1, 64))
                                                        .executes(context ->
                                                                CraftCommand.execute(
                                                                        context.getSource(),
                                                                        StringArgumentType.getString(context, "item"),
                                                                        IntegerArgumentType.getInteger(context, "count")
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(literal("recipe")
                                        .then(argument("index", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        CraftCommand.executeRecipe(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "index") - 1,
                                                                1
                                                        )
                                                )
                                                .then(argument("count", IntegerArgumentType.integer(1, 64))
                                                        .executes(context ->
                                                                CraftCommand.executeRecipe(
                                                                        context.getSource(),
                                                                        IntegerArgumentType.getInteger(context, "index") - 1,
                                                                        IntegerArgumentType.getInteger(context, "count")
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(literal("entry")
                                        .then(argument("index", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        CraftCommand.executeEntry(
                                                                context.getSource(),
                                                                IntegerArgumentType.getInteger(context, "index") - 1,
                                                                1
                                                        )
                                                )
                                                .then(argument("count", IntegerArgumentType.integer(1, 64))
                                                        .executes(context ->
                                                                CraftCommand.executeEntry(
                                                                        context.getSource(),
                                                                        IntegerArgumentType.getInteger(context, "index") - 1,
                                                                        IntegerArgumentType.getInteger(context, "count")
                                                                )
                                                        )
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("moveitem")
                                .then(argument("first", IntegerArgumentType.integer())
                                        .then(argument("second", IntegerArgumentType.integer())
                                                .then(argument("action", StringArgumentType.string())
                                                        .suggests(ClientSuggestionProviders.MOVE_ACTION_SUGGESTIONS)
                                                        .executes(context ->
                                                                MoveItemCommand.execute(
                                                                        context.getSource(),
                                                                        IntegerArgumentType.getInteger(context, "first"),
                                                                        IntegerArgumentType.getInteger(context, "second"),
                                                                        StringArgumentType.getString(context, "action")
                                                                )
                                                        )
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("openinventory")
                                .executes(context ->
                                        OpenInventoryCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("closescreen")
                                .executes(context ->
                                        CloseScreenCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );

        // key commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("presskey")
                                .then(argument("key", StringArgumentType.string())
                                        .executes(context ->
                                                KeyBindingCommand.executeTimedHold(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "key"),
                                                        250
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("holdkey")
                                .then(argument("key", StringArgumentType.string())
                                        .executes(context ->
                                                KeyBindingCommand.executeHold(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "key")
                                                )
                                        )
                                        .then(argument("seconds", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        KeyBindingCommand.executeTimedHold(
                                                                context.getSource(),
                                                                StringArgumentType.getString(context, "key"),
                                                                IntegerArgumentType.getInteger(context, "seconds") * 1000
                                                        )
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("releasekey")
                                .then(argument("key", StringArgumentType.string())
                                        .executes(context ->
                                                KeyBindingCommand.executeRelease(
                                                        context.getSource(),
                                                        StringArgumentType.getString(context, "key")
                                                )
                                        )
                                )
                )
        );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                        literal("releaseallkeys")
                                .executes(context ->
                                        ReleaseAllKeysCommand.execute(
                                                context.getSource()
                                        )
                                )
                )
        );


    }
}
