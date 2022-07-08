package cn.nukkit.entity.ai.route;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zzz1999 daoge_cmd @ MobPlugin
 */
public class RouteFinderThreadPool {

    public static ThreadPoolExecutor executor =
            new ThreadPoolExecutor(
                    1,
                    Runtime.getRuntime().availableProcessors() + 1,
                    1, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    new ThreadPoolExecutor.AbortPolicy()
            );

    public static void executeRouteFinderThread(RouteFinderSearchTask t) {
        if (!executor.isShutdown() && !executor.isTerminating()) executor.execute(t);
    }

    public static void shutDownNow() {
        executor.shutdownNow();
    }
}
