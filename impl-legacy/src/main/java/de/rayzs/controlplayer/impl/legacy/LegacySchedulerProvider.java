package de.rayzs.controlplayer.impl.legacy;

import de.rayzs.controlplayer.api.scheduler.SchedulerProvider;
import de.rayzs.controlplayer.api.scheduler.SchedulerTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.function.Consumer;

public class LegacySchedulerProvider implements SchedulerProvider {

    private final JavaPlugin plugin;

    public LegacySchedulerProvider(final JavaPlugin plugin) {
        this.plugin = plugin;
    }


    // Sync schedulers

    @Override
    public SchedulerTask createScheduler(final Consumer<SchedulerTask> scheduler) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!schedulerTask.isRunning()) {
                    this.cancel();
                    return;
                }

                scheduler.accept(schedulerTask);
            }
        }.runTask(plugin);

        return schedulerTask;
    }

    @Override
    public SchedulerTask createScheduler(final Consumer<SchedulerTask> scheduler, final long delay) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!schedulerTask.isRunning()) {
                    this.cancel();
                    return;
                }

                scheduler.accept(schedulerTask);
            }
        }.runTaskLater(plugin, delay);

        return schedulerTask;
    }

    @Override
    public SchedulerTask createScheduler(final Consumer<SchedulerTask> scheduler, final long delay, final long period) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!schedulerTask.isRunning()) {
                    this.cancel();
                    return;
                }

                scheduler.accept(schedulerTask);
            }
        }.runTaskTimer(plugin, delay, period);

        return schedulerTask;
    }


    // Async schedulers

    @Override
    public SchedulerTask createAsyncScheduler(final Consumer<SchedulerTask> scheduler) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!schedulerTask.isRunning()) {
                    this.cancel();
                    return;
                }

                scheduler.accept(schedulerTask);
            }
        }.runTaskAsynchronously(plugin);

        return schedulerTask;
    }

    @Override
    public SchedulerTask createAsyncScheduler(final Consumer<SchedulerTask> scheduler, final long delay) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!schedulerTask.isRunning()) {
                    this.cancel();
                    return;
                }

                scheduler.accept(schedulerTask);
            }
        }.runTaskLaterAsynchronously(plugin, delay);

        return schedulerTask;
    }

    @Override
    public SchedulerTask createAsyncScheduler(final Consumer<SchedulerTask> scheduler, final long delay, final long period) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!schedulerTask.isRunning()) {
                    this.cancel();
                    return;
                }

                scheduler.accept(schedulerTask);
            }
        }.runTaskTimerAsynchronously(plugin, delay, period);

        return schedulerTask;
    }
}