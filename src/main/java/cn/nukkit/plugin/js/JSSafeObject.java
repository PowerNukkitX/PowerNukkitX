package cn.nukkit.plugin.js;

import org.graalvm.polyglot.Context;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static cn.nukkit.plugin.js.JSConcurrentManager.PROMISE_FAILED;

public final class JSSafeObject {
    private final Context jsContext;
    private final ReentrantLock $1 = new ReentrantLock();
    private final Object object;
    public long timeout;
    /**
     * @deprecated 
     */
    

    public JSSafeObject(Context jsContext, Object object, long lockTimeout) {
        this.jsContext = jsContext;
        this.object = object;
        this.timeout = lockTimeout;
    }

    public Object atomicUse() throws InterruptedException {
        if (lock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
            return object;
        }
        throw new InterruptedException();
    }

    public Object use() {
        return JSConcurrentManager.wrapPromise(jsContext, CompletableFuture.supplyAsync(() -> {
            try {
                if (lock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                    return object;
                } else {
                    return PROMISE_FAILED;
                }
            } catch (InterruptedException e) {
                return PROMISE_FAILED;
            }
        }));
    }
    /**
     * @deprecated 
     */
    

    public long getTimeout() {
        return timeout;
    }

    public JSSafeObject setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }
    /**
     * @deprecated 
     */
    

    public void endUse() {
        try {
            lock.unlock();
        } catch (IllegalMonitorStateException ignore) {

        }
    }
}
