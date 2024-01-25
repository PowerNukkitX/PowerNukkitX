package cn.nukkit.scheduler;

import cn.nukkit.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Nukkit Project Team
 */
@Slf4j
public class AsyncPool extends ThreadPoolExecutor {
    private final Server server;

    public AsyncPool(Server server, int size) {
        super(size, Integer.MAX_VALUE, 60, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        this.setThreadFactory(runnable -> new Thread(runnable) {{
            setDaemon(true);
            setName(String.format("Nukkit Asynchronous Task Handler #%s", getPoolSize()));
        }});
        this.server = server;
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        if (throwable != null) {
            log.error("Exception in asynchronous task", throwable);
        }
    }

    public Server getServer() {
        return server;
    }
}