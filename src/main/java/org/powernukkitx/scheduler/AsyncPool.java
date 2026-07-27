package org.powernukkitx.scheduler;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nukkit Project Team
 */
@Slf4j
public class AsyncPool extends ThreadPoolExecutor {

    private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();

    public AsyncPool(int size) {
        super(size, Integer.MAX_VALUE, 30, TimeUnit.SECONDS, new SynchronousQueue<>());
        this.setThreadFactory(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName(String.format("Nukkit Asynchronous Task Handler #%s", THREAD_COUNTER.incrementAndGet()));
            return thread;
        });
        this.allowCoreThreadTimeOut(true);
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        if (throwable != null) {
            log.error("Exception in asynchronous task", throwable);
        }
    }
}