package cn.nukkit.utils;

import java.util.concurrent.TimeUnit;

/**
 * A thread that forcefully kills the server process if it takes too long to stop.
 * Useful for ensuring the JVM does not hang indefinitely during shutdown.
 *
 * @author MagicDroidX (Nukkit Project)
 */
public final class ServerKiller extends Thread {

    private final long sleepTime;

    /**
     * Creates a ServerKiller that waits the given time in seconds before killing the server.
     * @param time the time to wait (in seconds)
     */
    public ServerKiller(long time) {
        this(time, TimeUnit.SECONDS);
    }

    /**
     * Creates a ServerKiller that waits the given time in the specified unit before killing the server.
     * @param time the time to wait
     * @param unit the time unit
     */
    public ServerKiller(long time, TimeUnit unit) {
        this.sleepTime = unit.toMillis(time);
        setDaemon(true);
        setName("Server Killer");
    }

    @Override
    public void run() {
        try {
            Thread.sleep(sleepTime);
            System.err.println("\nTook too long to stop, server was killed forcefully!\n");
            System.exit(1);
        } catch (InterruptedException e) {
            // The thread was interrupted, which might mean that Ctrl+C was pressed
            System.err.println("\nServer stopping process was interrupted. Killing server...\n");
            System.exit(1);
        }
    }
}
