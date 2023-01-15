package cn.nukkit.entity.ai.route;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.route.finder.IRouteFinder;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.security.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 寻路管理器，所有的寻路任务都应该提交到这个管理器中，管理器负责调度寻路任务，实现资源利用最大化
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class RouteFindingManager {
    private static final AtomicInteger threadCount = new AtomicInteger(0);
    protected static RouteFindingManager INSTANCE = new RouteFindingManager();
    protected final ExecutorService pool;

    protected RouteFindingManager() {
        pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), new RouteFindingPoolThreadFactory(), null, true);
    }

    public static RouteFindingManager getInstance() {
        return INSTANCE;
    }

    public void submit(@NotNull RouteFindingTask task) {
        task.setStartTime(Server.getInstance().getNextTick());
        ((ForkJoinPool) pool).submit(task);
    }

    public static final class RouteFindingThread extends ForkJoinWorkerThread {
        /**
         * Creates a ForkJoinWorkerThread operating in the given pool.
         *
         * @param pool the pool this thread works in
         * @throws NullPointerException if pool is null
         */
        RouteFindingThread(ForkJoinPool pool) {
            super(pool);
            this.setName("RouteFindingThread" + threadCount.getAndIncrement());
            this.setPriority(2); // 保证主线程能得到足够多的CPU时间
        }
    }

    public static final class RouteFindingPoolThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
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
            return AccessController.doPrivileged((PrivilegedAction<ForkJoinWorkerThread>) () -> new RouteFindingThread(pool), ACC);
        }
    }

    public static class RouteFindingTask extends RecursiveAction {
        private final IRouteFinder routeFinder;
        private final AtomicLong startTime;
        private final AtomicBoolean started;
        private final AtomicBoolean finished;
        private final FinishCallback onFinish;
        private Vector3 start;
        private Vector3 target;

        public RouteFindingTask(IRouteFinder routeFinder, FinishCallback onFinish) {
            this.routeFinder = routeFinder;
            this.onFinish = onFinish;
            this.startTime = new AtomicLong(0);
            this.started = new AtomicBoolean(false);
            this.finished = new AtomicBoolean(false);
        }

        public Vector3 getStart() {
            return start;
        }

        public RouteFindingTask setStart(Vector3 start) {
            this.start = start;
            return this;
        }

        public Vector3 getTarget() {
            return target;
        }

        public RouteFindingTask setTarget(Vector3 target) {
            this.target = target;
            return this;
        }

        /**
         * @return 是否开始寻路
         */
        public boolean getStarted() {
            return started.get();
        }

        public void setStarted(boolean started) {
            this.started.set(started);
        }

        /**
         * @return 是否已经完成寻路，寻路失败也会返回完成
         */
        public boolean getFinished() {
            return finished.get();
        }

        protected void setFinished(boolean finished) {
            this.finished.set(finished);
        }

        public long getStartTime() {
            return startTime.get();
        }

        public RouteFindingTask setStartTime(long startTime) {
            this.startTime.set(startTime);
            return this;
        }

        @Override
        protected void compute() {
            setStarted(true);
            routeFinder.setStart(start);
            routeFinder.setTarget(target);
            routeFinder.search();
            setFinished(true);
            onFinish.onFinish(this);
        }

        public interface FinishCallback {
            void onFinish(RouteFindingTask task);
        }
    }
}
