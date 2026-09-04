package de.rayzs.controlplayer.api.scheduler;

import java.util.function.Consumer;

public interface SchedulerProvider {

    /**
     * Creates a sync scheduler task.
     *
     * @param scheduler The task to run.
     * @return The created scheduler task.
     */
    SchedulerTask createScheduler(
            final Consumer<SchedulerTask> scheduler
    );

    /**
     * Creates a sync delayed scheduler task.
     *
     * @param scheduler The task to run.
     * @return The created scheduler task.
     */
    SchedulerTask createScheduler(
            final Consumer<SchedulerTask> scheduler,
            final long delay
    );

    /**
     * Creates a sync repeating scheduler task.
     *
     * @param scheduler The task to run.
     * @return The created scheduler task.
     */
    SchedulerTask createScheduler(
            final Consumer<SchedulerTask> scheduler,
            final long delay,
            final long period
    );


    /**
     * Creates an async scheduler task.
     *
     * @param scheduler The task to run.
     * @return The created scheduler task.
     */
    SchedulerTask createAsyncScheduler(
            final Consumer<SchedulerTask> scheduler
    );

    /**
     * Creates an async delayed scheduler task.
     *
     * @param scheduler The task to run.
     * @return The created scheduler task.
     */
    SchedulerTask createAsyncScheduler(
            final Consumer<SchedulerTask> scheduler,
            final long delay
    );

    /**
     * Creates an async repeating scheduler task.
     *
     * @param scheduler The task to run.
     * @return The created scheduler task.
     */
    SchedulerTask createAsyncScheduler(
            final Consumer<SchedulerTask> scheduler,
            final long delay,
            final long period
    );
}