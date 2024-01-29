package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.console.NukkitConsole;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    public void run() {
        try {
            sleep(sleepTime);
            System.out.println("\nTook too long to stop, server was killed forcefully!\n");
            System.exit(1);
        } catch (InterruptedException e) {
            // The thread was interrupted, which might mean that Ctrl+C was pressed
            // Support Ctrl+C
            System.out.println("\nServer stopping process was interrupted. Killing server...\n");
            NukkitConsole console = new NukkitConsole(Server.getInstance());
            try {
                Method shutdownMethod = NukkitConsole.class.getDeclaredMethod("shutdown");
                shutdownMethod.setAccessible(true);
                shutdownMethod.invoke(console);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
    }
}
