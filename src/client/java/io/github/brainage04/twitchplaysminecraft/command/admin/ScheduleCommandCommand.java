package io.github.brainage04.twitchplaysminecraft.command.admin;

import io.github.brainage04.twitchplaysminecraft.util.RunnableScheduler;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@SuppressWarnings("SameReturnValue")
public class ScheduleCommandCommand {
    public static int execute(FabricClientCommandSource source, String command, int delay) {
        if (source.getClient().getNetworkHandler() == null) return 0;

        // throws a ConcurrentModificationException when iterator.remove(); is executed in RunnableScheduler
        RunnableScheduler.scheduleTask(() -> source.getClient().getNetworkHandler().sendChatCommand(command), delay);

        return 1;
    }

    public static int execute(FabricClientCommandSource source, String command) {
        return execute(source, command, 1);
    }
}
