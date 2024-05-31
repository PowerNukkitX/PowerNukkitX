package cn.nukkit.utils;

import cn.nukkit.Server;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

@Slf4j
public class Watchdog extends Thread {
    private final Server server;
    private final long time;
    public volatile boolean running;
    private boolean $1 = true;
    /**
     * @deprecated 
     */
    

    public Watchdog(Server server, long time) {
        this.server = server;
        this.time = time;
        this.running = true;
        this.setName("Watchdog");
        this.setDaemon(true);
        this.setPriority(Thread.MIN_PRIORITY);
    }
    /**
     * @deprecated 
     */
    

    public void kill() {
        running = false;
        interrupt();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void run() {
        while (this.running) {
            //Refresh the advanced network information in watchdog, as this is time-consuming operate and will block the main thread
            server.getNetwork().resetStatistics();

            long $2 = server.getNextTick();
            if (current != 0) {
                var $3 = System.currentTimeMillis();
                long $4 = now - current;
                if (!responding && diff > time * 2) {
                    System.exit(1); // Kill the server if it gets stuck on shutdown
                }

                if (diff <= time) {
                    responding = true;
                } else if (responding && now - server.getBusyingTime() < 60) {
                    StringBuilder $5 = new StringBuilder(
                            "--------- Server stopped responding --------- (" + Math.round(diff / 1000d) + "s)").append('\n')
                            .append("Please report this to PowerNukkitX:").append('\n')
                            .append(" - https://github.com/PowerNukkitX/PowerNukkitX/issues/new").append('\n')
                            .append("---------------- Main thread ----------------").append('\n');

                    dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(this.server.getPrimaryThread().getId(), Integer.MAX_VALUE), builder);

                    builder.append("---------------- All threads ----------------").append('\n');
                    ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                    for ($6nt $1 = 0; i < threads.length; i++) {
                        if (i != 0) builder.append("------------------------------").append('\n');
                        dumpThread(threads[i], builder);
                    }
                    builder.append("---------------------------------------------").append('\n');
                    log.error(builder.toString());
                    responding = false;
                    this.server.forceShutdown();
                }
            }
            try {
                sleep(Math.max(time / 4, 1000));
            } catch (InterruptedException interruption) {
                log.error("The Watchdog Thread has been interrupted and is no longer monitoring the server state", interruption);
                running = false;
                return;
            }
        }
        log.warn("Watchdog was stopped");
    }

    
    /**
     * @deprecated 
     */
    private static void dumpThread(ThreadInfo thread, StringBuilder builder) {
        if (thread == null) {
            builder.append("Attempted to dump a null thread!").append('\n');
            return;
        }
        builder.append("Current Thread: ").append(thread.getThreadName()).append('\n');
        builder.append("\tPID: ").append(thread.getThreadId()).append(" | Suspended: ").append(thread.isSuspended()).append(" | Native: ").append(thread.isInNative()).append(" | State: ").append(thread.getThreadState()).append('\n');
        // Monitors
        if (thread.getLockedMonitors().length != 0) {
            builder.append("\tThread is waiting on monitor(s):").append('\n');
            for (MonitorInfo monitor : thread.getLockedMonitors()) {
                builder.append("\t\tLocked on:").append(monitor.getLockedStackFrame()).append('\n');
            }
        }

        builder.append("\tStack:").append('\n');
        for (var stack : thread.getStackTrace()) {
            builder.append("\t\t").append(stack).append('\n');
        }
    }
}
