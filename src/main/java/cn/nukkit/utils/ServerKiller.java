package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;

import java.util.concurrent.TimeUnit;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ServerKiller extends Thread {

    public final long sleepTime;

    public ServerKiller(long time) {
        this(time, TimeUnit.SECONDS);
    }

    public ServerKiller(long time, TimeUnit unit) {
        this.sleepTime = unit.toMillis(time);
        this.setName("Server Killer");
    }

    @Override
    @PowerNukkitOnly
    public void run() {
        try {
            sleep(sleepTime);
            System.out.println("\nTook too long to stop, server was killed forcefully!\n");
            System.exit(1);
        } catch (InterruptedException e) {
            // The thread was interrupted, which might mean that Ctrl+C was pressed
            // Support Ctrl+C
            System.out.println("\nServer stopping process was interrupted. Killing server...\n");
            System.exit(1);
        }
    }
}
