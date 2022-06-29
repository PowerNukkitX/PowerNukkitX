package cn.nukkit.plugin.js;

import cn.nukkit.plugin.CommonJSPlugin;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class JSConcurrentManager {
    public static final Object PROMISE_FAILED = new Object();
    private long lockTimeout = 30000;

    private final CommonJSPlugin jsPlugin;

    public JSConcurrentManager(CommonJSPlugin jsPlugin) {
        this.jsPlugin = jsPlugin;
    }

    public JSSafeObject warpSafe(Object object) {
        return new JSSafeObject(jsPlugin.getJsContext(), object, lockTimeout);
    }

    public JSWorker createWorker(String sourcePath) {
        return new JSWorker(jsPlugin.getJsContext(), jsPlugin.getFileSystem(), sourcePath);
    }

    public JSJob createJob(String sourcePath) {
        return new JSJob(jsPlugin.getJsContext(), jsPlugin.getFileSystem(), sourcePath);
    }

    public long getLockTimeout() {
        return lockTimeout;
    }

    public JSConcurrentManager setLockTimeout(long lockTimeout) {
        this.lockTimeout = lockTimeout;
        return this;
    }

    public interface Thenable {
        void then(Value onResolve, Value onReject);
    }

    @FunctionalInterface
    public interface Executable {
        void onPromiseCreation(Value onResolve, Value onReject);
    }

    public static JPromise wrapPromise(Context context, CompletableFuture<?> javaFuture) {
        return new JPromise(context, javaFuture);
    }

    public static final class JPromise implements Thenable, Executable {
        private final Context context;
        private final CompletableFuture<?> javaFuture;

        public JPromise(Context context, CompletableFuture<?> javaFuture) {
            this.context = context;
            this.javaFuture = javaFuture;
        }

        public void then(Value onResolve, Value onReject) {
            javaFuture.whenComplete((result, ex) -> {
                synchronized (context) {
                    if (result != PROMISE_FAILED) {
                        onResolve.execute(result);
                    } else {
                        onReject.execute(ex);
                    }
                }
            });
        }

        public void then(Value onResolve) {
            javaFuture.whenComplete((result, ex) -> {
                synchronized (context) {
                    if (result != PROMISE_FAILED) {
                        onResolve.execute(result);
                    }
                }
            });
        }

        public void onPromiseCreation(Value onResolve, Value onReject) {
            javaFuture.whenComplete((result, ex) -> {
                synchronized (context) {
                    if (result != PROMISE_FAILED) {
                        onResolve.execute(result);
                    } else {
                        onReject.execute(ex);
                    }
                }
            });
        }

        public Object waitAndGet() throws ExecutionException, InterruptedException {
            synchronized (context) {
                return javaFuture.get();
            }
        }

        public Object waitAndGet(long timeOut) throws ExecutionException, InterruptedException, TimeoutException {
            synchronized (context) {
                return javaFuture.get(timeOut, TimeUnit.MILLISECONDS);
            }
        }

        public Object join() {
            synchronized (context) {
                return javaFuture.join();
            }
        }
    }
}
