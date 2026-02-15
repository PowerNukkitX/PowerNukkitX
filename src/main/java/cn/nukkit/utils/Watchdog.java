package cn.nukkit.utils;

import cn.nukkit.Server;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

/**
 * Watchdog monitors the main server thread and forces shutdown if the server becomes unresponsive.
 */
@Slf4j
public class Watchdog extends Thread {
    private final Server server;
    private final long timeoutMs;
    public volatile boolean running;
    private boolean responding = true;

    /**
     * Creates a new Watchdog.
     * @param server the server to monitor
     * @param timeoutMs maximum inactivity delay (in ms) before detecting a freeze
     */
    public Watchdog(Server server, long timeoutMs) {
        this.server = server;
        this.timeoutMs = timeoutMs;
        this.running = true;
        setName("Watchdog");
        setDaemon(true);
        setPriority(Thread.MIN_PRIORITY);
    }

    /**
     * Stops the Watchdog gracefully.
     */
    public void kill() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        while (running) {
            // Refresh network statistics (expensive operation)
            server.getNetwork().resetStatistics();

            long nextTick = server.getNextTick();
            if (nextTick != 0) {
                long now = System.currentTimeMillis();
                long diff = now - nextTick;
                if (!responding && diff > timeoutMs * 2) {
                    System.exit(1); // Kill the server if stuck during shutdown
                }

                if (diff <= timeoutMs) {
                    responding = true;
                } else if (responding && now - server.getBusyingTime() < 60) {
                    StringBuilder builder = new StringBuilder()
                            .append("--------- Server stopped responding --------- (")
                            .append(Math.round(diff / 1000d)).append("s)\n")
                            .append("Please report this to PowerNukkitX:\n")
                            .append(" - https://github.com/PowerNukkitX/PowerNukkitX/issues/new\n")
                            .append("---------------- Main thread ----------------\n");

                    dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(
                            server.getPrimaryThread().threadId(), Integer.MAX_VALUE), builder);

                    builder.append("---------------- All threads ----------------\n");
                    ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                    for (int i = 0; i < threads.length; i++) {
                        if (i != 0) builder.append("------------------------------\n");
                        dumpThread(threads[i], builder);
                    }
                    builder.append("---------------------------------------------\n");
                    log.error(builder.toString());
                    responding = false;
                    server.forceShutdown();
                }
            }
            try {
                sleep(Math.max(timeoutMs / 4, 1000));
            } catch (InterruptedException interruption) {
                log.error("The Watchdog Thread has been interrupted and is no longer monitoring the server state", interruption);
                running = false;
                Thread.currentThread().interrupt();
                return;
            }
        }
        log.warn("Watchdog was stopped");
    }

    /**
     * Appends a thread dump to the StringBuilder.
     * @param thread the thread to dump
     * @param builder the target StringBuilder
     */
    private static void dumpThread(ThreadInfo thread, StringBuilder builder) {
        if (thread == null) {
            builder.append("Attempted to dump a null thread!\n");
            return;
        }
        builder.append("Current Thread: ").append(thread.getThreadName()).append('\n');
        builder.append("\tPID: ").append(thread.getThreadId())
                .append(" | Suspended: ").append(thread.isSuspended())
                .append(" | Native: ").append(thread.isInNative())
                .append(" | State: ").append(thread.getThreadState()).append('\n');
        // Monitors
        if (thread.getLockedMonitors().length != 0) {
            builder.append("\tThread is waiting on monitor(s):\n");
            for (MonitorInfo monitor : thread.getLockedMonitors()) {
                builder.append("\t\tLocked on:").append(monitor.getLockedStackFrame()).append('\n');
            }
        }
        builder.append("\tStack:\n");
        for (var stack : thread.getStackTrace()) {
            builder.append("\t\t").append(stack).append('\n');
        }
    }
}
