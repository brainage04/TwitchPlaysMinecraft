package io.github.brainage04.twitchplaysminecraft.util;

import io.github.brainage04.twitchplaysminecraft.TwitchPlaysMinecraft;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RunnableScheduler {
    public static final int startDelayTicks = 2;

    private static class ScheduledTask {
        public final Runnable runnable;
        public int ticksPassed;
        public final int delay;

        public ScheduledTask(Runnable runnable, int delay) {
            this.runnable = runnable;
            this.delay = delay;
        }
    }

    private static final List<ScheduledTask> scheduledTasks = new ArrayList<>();

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (scheduledTasks.isEmpty()) return;

            Iterator<ScheduledTask> iterator = scheduledTasks.iterator();
            while (iterator.hasNext()) {
                ScheduledTask task = iterator.next();
                task.ticksPassed++;

                if (task.ticksPassed >= task.delay) {
                    client.execute(task.runnable);
                    iterator.remove();
                }
            }
        });
    }

    public static void scheduleTask(Runnable runnable, int delay) {
        if (delay < 1) {
            TwitchPlaysMinecraft.LOGGER.error("Runnable scheduled for less than 1 tick in the future - cancelling!");
            return;
        }

        scheduledTasks.add(new ScheduledTask(runnable, delay));
    }
}
