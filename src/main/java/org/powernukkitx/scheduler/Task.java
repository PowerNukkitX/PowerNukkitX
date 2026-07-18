package org.powernukkitx.scheduler;

import lombok.extern.slf4j.Slf4j;

/**
 * A class that describes a task.
 *
 * <p>A task can be executed by Nukkit server with a/an express, delay, repeat or delay&amp;repeat.
 * See:{@link ServerScheduler}</p>
 *
 * <p>For plugin developers: To make sure your task will only be executed in the case of safety
 * (such as: prevent this task from running if its owner plugin is disabled),
 * it's suggested to use {@link PluginTask} instead of extend this class.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@Slf4j
public abstract class Task implements Runnable {
    private TaskHandler taskHandler = null;

    public final TaskHandler getHandler() {
        return this.taskHandler;
    }

    public final int getTaskId() {
        return this.taskHandler != null ? this.taskHandler.getTaskId() : -1;
    }

    public final void setHandler(TaskHandler taskHandler) {
        if (this.taskHandler == null || taskHandler == null) {
            this.taskHandler = taskHandler;
        }
    }

    /**
     * What will be called when the task is executed.
     *
     * @param currentTick The elapsed tick count from the server is started. 20ticks = 1second, 1tick = 0.05second.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    public abstract void onRun(int currentTick);

    @Override
    public final void run() {
        this.onRun(taskHandler.getLastRunTick());
    }

    public void onCancel() {

    }

    public void cancel() {
        try {
            this.getHandler().cancel();
        } catch (RuntimeException ex) {
            log.error("Exception while invoking onCancel", ex);
        }
    }

}
