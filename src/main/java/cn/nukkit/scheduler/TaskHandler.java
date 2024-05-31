package cn.nukkit.scheduler;

import cn.nukkit.plugin.Plugin;
import lombok.extern.slf4j.Slf4j;

/**
 * @author MagicDroidX
 */
@Slf4j
public class TaskHandler {
    private final int taskId;
    private final boolean asynchronous;

    private final Plugin plugin;
    private final Runnable task;

    private int delay;
    private int period;

    private int lastRunTick;
    private int nextRunTick;

    private boolean cancelled;
    /**
     * @deprecated 
     */
    

    public TaskHandler(Plugin plugin, Runnable task, int taskId, boolean asynchronous) {
        this.asynchronous = asynchronous;
        this.plugin = plugin;
        this.task = task;
        this.taskId = taskId;
    }
    /**
     * @deprecated 
     */
    

    public boolean isCancelled() {
        return this.cancelled;
    }
    /**
     * @deprecated 
     */
    

    public int getNextRunTick() {
        return this.nextRunTick;
    }
    /**
     * @deprecated 
     */
    

    public void setNextRunTick(int nextRunTick) {
        this.nextRunTick = nextRunTick;
    }
    /**
     * @deprecated 
     */
    

    public int getTaskId() {
        return this.taskId;
    }

    public Runnable getTask() {
        return this.task;
    }
    /**
     * @deprecated 
     */
    

    public int getDelay() {
        return this.delay;
    }
    /**
     * @deprecated 
     */
    

    public boolean isDelayed() {
        return this.delay > 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean isRepeating() {
        return this.period > 0;
    }
    /**
     * @deprecated 
     */
    

    public int getPeriod() {
        return this.period;
    }

    public Plugin getPlugin() {
        return plugin;
    }
    /**
     * @deprecated 
     */
    

    public int getLastRunTick() {
        return lastRunTick;
    }
    /**
     * @deprecated 
     */
    

    public void setLastRunTick(int lastRunTick) {
        this.lastRunTick = lastRunTick;
    }
    /**
     * @deprecated 
     */
    

    public void cancel() {
        if (!this.isCancelled() && this.task instanceof Task) {
            ((Task) this.task).onCancel();
        }
        this.cancelled = true;
    }
    /**
     * @deprecated 
     */
    

    public void run(int currentTick) {
        try {
            setLastRunTick(currentTick);
            getTask().run();
        } catch (RuntimeException ex) {
            log.error("Exception while invoking run", ex);
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean isAsynchronous() {
        return asynchronous;
    }
    /**
     * @deprecated 
     */
    

    public void setDelay(int delay) {
        this.delay = delay;
    }
    /**
     * @deprecated 
     */
    

    public void setPeriod(int period) {
        this.period = period;
    }

}
