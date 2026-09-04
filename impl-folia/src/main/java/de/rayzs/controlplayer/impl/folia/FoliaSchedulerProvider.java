package de.rayzs.controlplayer.impl.folia;

import de.rayzs.controlplayer.api.scheduler.SchedulerProvider;
import de.rayzs.controlplayer.api.scheduler.SchedulerTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FoliaSchedulerProvider implements SchedulerProvider {

    private final JavaPlugin plugin;

    public FoliaSchedulerProvider(final JavaPlugin plugin) {
        this.plugin = plugin;
    }


    // Sync schedulers

    @Override
    public SchedulerTask createScheduler(final Consumer<SchedulerTask> scheduler) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        Bukkit.getGlobalRegionScheduler().run(plugin, s -> {
            if (!schedulerTask.isRunning()) {
                s.cancel();
                return;
            }

            scheduler.accept(schedulerTask);
        });

        return schedulerTask;
    }

    @Override
    public SchedulerTask createScheduler(final Consumer<SchedulerTask> scheduler, final long delay) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, s -> {
            if (!schedulerTask.isRunning()) {
                s.cancel();
                return;
            }

            scheduler.accept(schedulerTask);
        }, delay);

        return schedulerTask;
    }

    @Override
    public SchedulerTask createScheduler(final Consumer<SchedulerTask> scheduler, final long delay, final long period) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, s -> {
            if (!schedulerTask.isRunning()) {
                s.cancel();
                return;
            }

            scheduler.accept(schedulerTask);
        }, delay, period);

        return schedulerTask;
    }


    // Async schedulers

    @Override
    public SchedulerTask createAsyncScheduler(final Consumer<SchedulerTask> scheduler) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        Bukkit.getAsyncScheduler().runNow(plugin, s -> {
            if (!schedulerTask.isRunning()) {
                s.cancel();
                return;
            }

            scheduler.accept(schedulerTask);
        });

        return schedulerTask;
    }

    @Override
    public SchedulerTask createAsyncScheduler(final Consumer<SchedulerTask> scheduler, final long delay) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        Bukkit.getAsyncScheduler().runDelayed(plugin, s -> {
            if (!schedulerTask.isRunning()) {
                s.cancel();
                return;
            }

            scheduler.accept(schedulerTask);
        }, delay * 50, TimeUnit.MILLISECONDS);

        return schedulerTask;
    }

    @Override
    public SchedulerTask createAsyncScheduler(final Consumer<SchedulerTask> scheduler, final long delay, final long period) {
        final SchedulerTask schedulerTask = new SchedulerTask();

        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, s -> {
            if (!schedulerTask.isRunning()) {
                s.cancel();
                return;
            }

            scheduler.accept(schedulerTask);
        }, delay * 50, period * 50, TimeUnit.MILLISECONDS);

        return schedulerTask;
    }
}