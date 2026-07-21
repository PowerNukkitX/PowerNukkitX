package org.powernukkitx.scheduler;

import org.powernukkitx.plugin.Plugin;
import org.powernukkitx.utils.PluginException;
import org.powernukkitx.utils.Utils;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nukkit Project Team
 */
@Slf4j
public class ServerScheduler {
    public static int WORKERS = 4;
    private final AsyncPool asyncPool;

    private final Queue<TaskHandler> pending;
    private final Map<Integer, ArrayDeque<TaskHandler>> queueMap;
    private final Map<Integer, TaskHandler> taskMap;
    private final AtomicInteger currentTaskId;

    private volatile int currentTick = -1;

    public ServerScheduler() {
        this.pending = new ConcurrentLinkedQueue<>();
        this.currentTaskId = new AtomicInteger();
        this.queueMap = new ConcurrentHashMap<>();
        this.taskMap = new ConcurrentHashMap<>();
        this.asyncPool = new AsyncPool(WORKERS);
    }

    /**
     * Set a task to be executed only once,delay=0 period=0 asynchronous=false
     *
     * @param task the task
     * @return the task handler
     */
    public TaskHandler scheduleTask(Task task) {
        return addTask(task, 0, 0, false);
    }

    /**
     * Schedule a task using runnable
     *
     * @param task the task
     * @return the task handler
     */
    public TaskHandler scheduleTask(Runnable task) {
        return addTask(null, task, 0, 0, false);
    }

    /**
     * Schedule a task using runnable
     *
     * @param task         the task
     * @param asynchronous whether is executed in other thread(not main thread)
     * @return the task handler
     */
    public TaskHandler scheduleTask(Runnable task, boolean asynchronous) {
        return addTask(null, task, 0, 0, asynchronous);
    }

    /**
     * Schedule a async task
     * <p>
     * the asynctask is executed in other thread(not main thread)
     *
     * @param task the async task
     * @return the task handler
     */
    public TaskHandler scheduleAsyncTask(AsyncTask task) {
        return addTask(null, task, 0, 0, true);
    }

    /**
     * Schedule async task to worker.
     *
     * @param task   the task
     * @param worker the worker
     */
    public void scheduleAsyncTaskToWorker(AsyncTask task, int worker) {
        scheduleAsyncTask(task);
    }

    /**
     * Schedule a task with delay.
     *
     * @param task         the task
     * @param delay        the delay, use game tick,20tick = 1s
     * @param asynchronous whether is executed in other thread(not main thread)
     * @return the task handler
     */
    public TaskHandler scheduleDelayedTask(Runnable task, int delay, boolean asynchronous) {
        return addTask(null, task, delay, 0, asynchronous);
    }

    /**
     * Schedule a task with delay and repeat to execute.
     *
     * @param task         the task
     * @param period       the period of repeat,use game tick,20tick = 1s
     * @param asynchronous whether is executed in other thread(not main thread)
     * @return the task handler
     */
    public TaskHandler scheduleRepeatingTask(Runnable task, int period, boolean asynchronous) {
        return addTask(null, task, 0, period, asynchronous);
    }

    /**
     * Schedule delayed repeating task
     *
     * @param task   the task
     * @param delay  the delay, use game tick,20tick = 1s
     * @param period the period of repeat,use game tick,20tick = 1s
     * @return the task handler
     */
    public TaskHandler scheduleDelayedRepeatingTask(Runnable task, int delay, int period) {
        return addTask(null, task, delay, period, false);
    }

    /**
     * Schedule delayed repeating task.
     *
     * @param task         the task
     * @param delay        the delay, use game tick,20tick = 1s
     * @param period       the period of repeat,use game tick,20tick = 1s
     * @param asynchronous whether is executed in other thread(not main thread)
     * @return the task handler
     */
    public TaskHandler scheduleDelayedRepeatingTask(Runnable task, int delay, int period, boolean asynchronous) {
        return addTask(null, task, delay, period, asynchronous);
    }

    /**
     * Set a task to be executed only once,delay=0 period=0 asynchronous=false
     *
     * @param plugin the plugin
     * @param task   the task
     * @return the task handler
     */
    public TaskHandler scheduleTask(Plugin plugin, Runnable task) {
        return addTask(plugin, task, 0, 0, false);
    }

    /**
     * Set a task to be executed only once,delay=0 period=0
     *
     * @param plugin       the plugin
     * @param task         the task
     * @param asynchronous Whether it executes asynchronously
     * @return the task handler
     */
    public TaskHandler scheduleTask(Plugin plugin, Runnable task, boolean asynchronous) {
        return addTask(plugin, task, 0, 0, asynchronous);
    }

    /**
     * Set up an asynchronous task to be executed only once
     *
     * @param plugin the plugin instance
     * @param task   the asynchronous task
     */
    public TaskHandler scheduleAsyncTask(Plugin plugin, AsyncTask task) {
        return addTask(plugin, task, 0, 0, true);
    }

    public int getAsyncTaskPoolSize() {
        return asyncPool.getCorePoolSize();
    }

    /**
     * Set up a delayed task to be executed only once
     *
     * @param task  the task, can be created with an anonymous class
     * @param delay the delay time, in ticks (20 ticks = 1s)
     */
    public TaskHandler scheduleDelayedTask(Task task, int delay) {
        return this.addTask(task, delay, 0, false);
    }

    /**
     * Set up a deferred task that executes only once
     *
     * @param task         Tasks, which can be created using lambda expressions
     * @param delay        Delay time, in tick (20tick = 1s)
     * @param asynchronous Whether it executes asynchronously, and if so, enables a new thread to execute the task
     * @return the task handler
     */
    public TaskHandler scheduleDelayedTask(Task task, int delay, boolean asynchronous) {
        return this.addTask(task, delay, 0, asynchronous);
    }

    /**
     * Use {@link #scheduleDelayedTask(Plugin, Runnable, int)}
     */
    public TaskHandler scheduleDelayedTask(Runnable task, int delay) {
        return addTask(null, task, delay, 0, false);
    }

    /**
     * Set up a non-asynchronous (execute on the main thread) deferred task that executes only once
     *
     * @param plugin the plugin
     * @param task   Tasks, which can be created using lambda expressions
     * @return the task handler
     */
    public TaskHandler scheduleDelayedTask(Plugin plugin, Runnable task, int delay) {
        return addTask(plugin, task, delay, 0, false);
    }

    /**
     * Schedule delayed task.
     *
     * @param plugin       the task executor (plugin)
     * @param task         the task
     * @param delay        the delay, use game tick,20tick = 1s
     * @param asynchronous whether is executed in other thread(not main thread)
     * @return the task handler
     */
    public TaskHandler scheduleDelayedTask(Plugin plugin, Runnable task, int delay, boolean asynchronous) {
        return addTask(plugin, task, delay, 0, asynchronous);
    }

    /**
     * Schedule repeating task .
     *
     * @param plugin the task executor (plugin)
     * @param task   the task
     * @param period the period of repeat,use game tick,20tick = 1s
     * @return the task handler
     */
    public TaskHandler scheduleRepeatingTask(Plugin plugin, Runnable task, int period) {
        return addTask(plugin, task, 0, period, false);
    }


    /**
     * Schedule repeating task .
     *
     * @param plugin       the task executor (plugin)
     * @param task         the task
     * @param period       the period of repeat,use game tick,20tick = 1s
     * @param asynchronous whether is executed in other thread(not main thread)
     * @return the task handler
     */
    public TaskHandler scheduleRepeatingTask(Plugin plugin, Runnable task, int period, boolean asynchronous) {
        return addTask(plugin, task, 0, period, asynchronous);
    }

    /**
     * Schedule repeating task .
     *
     * @param task   the task
     * @param period the period of repeat,use game tick,20tick = 1s
     * @return the task handler
     */
    public TaskHandler scheduleRepeatingTask(Task task, int period) {
        return addTask(task, 0, period, false);
    }

    /**
     * Schedule repeating task.
     *
     * @param task         the task
     * @param period       the period
     * @param asynchronous the asynchronous
     * @return the task handler
     */
    public TaskHandler scheduleRepeatingTask(Task task, int period, boolean asynchronous) {
        return addTask(task, 0, period, asynchronous);
    }

    /**
     * Schedule repeating task.
     *
     * @param task   the task
     * @param period the period
     * @return the task handler
     */
    public TaskHandler scheduleRepeatingTask(Runnable task, int period) {
        return addTask(null, task, 0, period, false);
    }

    /**
     * Schedule delayed repeating task.
     *
     * @param task   the task
     * @param delay  the delay
     * @param period the period
     * @return the task handler
     */
    public TaskHandler scheduleDelayedRepeatingTask(Task task, int delay, int period) {
        return addTask(task, delay, period, false);
    }

    /**
     * Schedule delayed repeating task.
     *
     * @param task         the task
     * @param delay        the delay
     * @param period       the period
     * @param asynchronous the asynchronous
     * @return the task handler
     */
    public TaskHandler scheduleDelayedRepeatingTask(Task task, int delay, int period, boolean asynchronous) {
        return addTask(task, delay, period, asynchronous);
    }


    /**
     * Schedule delayed repeating task.
     *
     * @param plugin the plugin
     * @param task   the task
     * @param delay  the delay
     * @param period the period
     * @return the task handler
     */
    public TaskHandler scheduleDelayedRepeatingTask(Plugin plugin, Runnable task, int delay, int period) {
        return addTask(plugin, task, delay, period, false);
    }


    /**
     * Set a deferral period task
     *
     * @param plugin       the plugin
     * @param task         the task
     * @param delay        The time to delay the start in tick
     * @param period       The time of the cycle execution, in tick
     * @param asynchronous Whether it executes asynchronously
     * @return the task handler
     */
    public TaskHandler scheduleDelayedRepeatingTask(Plugin plugin, Runnable task, int delay, int period, boolean asynchronous) {
        return addTask(plugin, task, delay, period, asynchronous);
    }

    public void cancelTask(int taskId) {
        if (taskMap.containsKey(taskId)) {
            try {
                taskMap.remove(taskId).cancel();
            } catch (RuntimeException ex) {
                log.error("Exception while invoking onCancel", ex);
            }
        }
    }

    /**
     * Cancel all task of specific plugin.
     *
     * @param plugin the specific plugin.
     */
    public void cancelTask(Plugin plugin) {
        Preconditions.checkNotNull(plugin);
        for (Map.Entry<Integer, TaskHandler> entry : taskMap.entrySet()) {
            TaskHandler taskHandler = entry.getValue();
            // TODO: Remove the "taskHandler.getPlugin() == null" check
            // It is only there for backwards compatibility!
            if (taskHandler.getPlugin() == null || plugin.equals(taskHandler.getPlugin())) {
                try {
                    taskHandler.cancel(); /* It will remove from task map automatic in next main heartbeat. */
                } catch (RuntimeException ex) {
                    log.error("Exception while invoking onCancel", ex);
                }
            }
        }
    }

    public void cancelAllTasks() {
        for (Map.Entry<Integer, TaskHandler> entry : this.taskMap.entrySet()) {
            try {
                entry.getValue().cancel();
            } catch (RuntimeException ex) {
                log.error("Exception while invoking onCancel", ex);
            }
        }
        this.taskMap.clear();
        this.queueMap.clear();
        this.currentTaskId.set(0);
    }

    public boolean isQueued(int taskId) {
        return this.taskMap.containsKey(taskId);
    }

    private TaskHandler addTask(Task task, int delay, int period, boolean asynchronous) {
        return addTask(task instanceof PluginTask ? ((PluginTask<?>) task).getOwner() : null, task, delay, period, asynchronous);
    }

    private TaskHandler addTask(Plugin plugin, Runnable task, int delay, int period, boolean asynchronous) {
        if (plugin != null && plugin.isDisabled()) {
            throw new PluginException("Plugin '" + plugin.getName() + "' attempted to register a task while disabled.");
        }
        if (delay < 0 || period < 0) {
            throw new PluginException("Attempted to register a task with negative delay or period.");
        }

        TaskHandler taskHandler = new TaskHandler(plugin, task, nextTaskId(), asynchronous);
        taskHandler.setDelay(delay);
        taskHandler.setPeriod(period);
        taskHandler.setNextRunTick(taskHandler.isDelayed() ? currentTick + taskHandler.getDelay() : currentTick);

        if (task instanceof Task) {
            ((Task) task).setHandler(taskHandler);
        }

        pending.offer(taskHandler);
        taskMap.put(taskHandler.getTaskId(), taskHandler);

        return taskHandler;
    }

    @ApiStatus.Internal
    public void mainThreadHeartbeat(int currentTick) {
        // Accepts pending.
        TaskHandler task;
        while ((task = pending.poll()) != null) {
            // Do not schedule in the past. Subtraction instead of Math.max so the
            // comparison stays correct when the tick counter wraps past Integer.MAX_VALUE
            // (reachable within hours at high tick rates).
            int next = task.getNextRunTick();
            int tick = (next - currentTick > 0) ? next : currentTick;
            ArrayDeque<TaskHandler> queue = Utils.getOrCreate(queueMap, tick, ArrayDeque::new);
            queue.add(task);
        }
        if (currentTick - this.currentTick > queueMap.size()) { // A large number of ticks have passed since the last execution
            for (Map.Entry<Integer, ArrayDeque<TaskHandler>> entry : queueMap.entrySet()) {
                int tick = entry.getKey();
                if (tick - currentTick <= 0) { // wraparound-safe "tick <= currentTick"
                    runTasks(tick);
                }
            }
        } else { // Normal server tick
            for (int i = this.currentTick + 1; i <= currentTick; i++) {
                runTasks(currentTick);
            }
        }
        this.currentTick = currentTick;
        AsyncTask.collectTask();
    }

    private void runTasks(int currentTick) {
        ArrayDeque<TaskHandler> queue = queueMap.remove(currentTick);
        if (queue != null) {
            for (TaskHandler taskHandler : queue) {
                if (taskHandler.isCancelled()) {
                    taskMap.remove(taskHandler.getTaskId());
                    continue;
                } else if (taskHandler.isAsynchronous()) {
                    asyncPool.execute(taskHandler.getTask());
                } else {
                    try {
                        taskHandler.run(currentTick);
                    } catch (Throwable e) {
                        log.error("Could not execute taskHandler {}", taskHandler.getTaskId(), e);
                    }
                }
                if (taskHandler.isRepeating()) {
                    taskHandler.setNextRunTick(currentTick + taskHandler.getPeriod());
                    pending.offer(taskHandler);
                } else {
                    try {
                        TaskHandler removed = taskMap.remove(taskHandler.getTaskId());
                        if (removed != null) removed.cancel();
                    } catch (RuntimeException ex) {
                        log.error("Exception while invoking onCancel", ex);
                    }
                }
            }
        }
    }

    public int getQueueSize() {
        int size = pending.size();
        for (ArrayDeque<TaskHandler> queue : queueMap.values()) {
            size += queue.size();
        }
        return size;
    }

    private int nextTaskId() {
        return currentTaskId.incrementAndGet();
    }

    public void close() {
        this.asyncPool.shutdownNow();
    }

    @ApiStatus.Internal
    public AsyncPool getAsyncTaskThreadPool() {
        return asyncPool;
    }
}
