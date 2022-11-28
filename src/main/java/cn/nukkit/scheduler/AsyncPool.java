package cn.nukkit.scheduler;

import cn.nukkit.Server;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.security.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * @author Nukkit Project Team
 */
@Log4j2
public class AsyncPool extends ForkJoinPool {
    private final Server server;

    public AsyncPool(Server server, int size) {
        super(Math.min(0x7fff, Math.min(Runtime.getRuntime().availableProcessors(), size)), new AsyncTaskPoolThreadFactory(), (t, e) -> log.fatal("Exception in asynchronous task,Thread:" + t.getName(), e), true);
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    private static class AsyncTaskThread extends ForkJoinWorkerThread {
        /**
         * Creates a ForkJoinWorkerThread operating in the given pool.
         *
         * @param pool the pool this thread works in
         * @throws NullPointerException if pool is null
         */
        AsyncTaskThread(ForkJoinPool pool) {
            super(pool);
            this.setDaemon(true);
            this.setName(String.format("Nukkit Asynchronous Task Handler #%s", pool.getPoolSize()));
        }
    }

    private static class AsyncTaskPoolThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        @SuppressWarnings("removal")
        private static final AccessControlContext ACC = contextWithPermissions(
                new RuntimePermission("getClassLoader"),
                new RuntimePermission("setContextClassLoader"));

        @SuppressWarnings("removal")
        static AccessControlContext contextWithPermissions(@NotNull Permission... perms) {
            Permissions permissions = new Permissions();
            for (var perm : perms)
                permissions.add(perm);
            return new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain(null, permissions)});
        }

        @SuppressWarnings("removal")
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return AccessController.doPrivileged((PrivilegedAction<ForkJoinWorkerThread>) () -> new AsyncTaskThread(pool), ACC);
        }
    }
}
