package de.rayzs.controlplayer.api.scheduler;

public class SchedulerTask {

    private boolean stopped = false;
    private Runnable stopAction = null;

    /**
     * Sets a runnable to be executed when the stop method is called.
     *
     * @param stopAction The runnable.
     */
    public void setStopAction(final Runnable stopAction) {
        if (this.stopAction == null) {
            this.stopAction = stopAction;
        }
    }


    /**
     * Says if the scheduler task is still running or not.
     *
     * @return true if the scheduler task is still running, false otherwise.
     */
    public boolean isRunning() {
        return !stopped;
    }

    /**
     * Stops the scheduler task and runs the stop action if it exists.
     */
    public void stop() {
        if (this.stopped) {
            return;
        }

        this.stopped = true;

        if (this.stopAction != null) {
            this.stopAction.run();
        }
    }
}