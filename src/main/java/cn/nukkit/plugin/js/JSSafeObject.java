package cn.nukkit.plugin.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

import static cn.nukkit.plugin.js.JSConcurrentManager.PROMISE_FAILED;

public final class JSSafeObject {
    private final Context jsContext;
    private final ReentrantLock lock = new ReentrantLock();
    private final Object object;

    public JSSafeObject(Context jsContext, Object object) {
        this.jsContext = jsContext;
        this.object = object;
    }

    public Value use() {
        return JSConcurrentManager.wrapPromise(jsContext, CompletableFuture.supplyAsync(() -> {
            if (lock.tryLock()) {
                return object;
            } else {
                return PROMISE_FAILED;
            }
        }));
    }

    public void endUse() {
        try {
            lock.unlock();
        } catch (IllegalMonitorStateException ignore) {

        }
    }
}
