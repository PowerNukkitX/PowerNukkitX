package cn.nukkit.plugin.js;

import cn.nukkit.plugin.CommonJSPlugin;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

public final class JSConcurrentManager {
    public static final Object PROMISE_FAILED = new Object();
    private static final Map<Context, Value> promiseConstructorCache = new WeakHashMap<>();

    private final CommonJSPlugin jsPlugin;

    public JSConcurrentManager(CommonJSPlugin jsPlugin) {
        this.jsPlugin = jsPlugin;
    }

    public JSSafeObject warpSafe(Object object) {
        return new JSSafeObject(jsPlugin.getJsContext(), object);
    }

    public JSWorker createWorker(String sourcePath) {
        return new JSWorker(jsPlugin.getJsContext(), jsPlugin.getFileSystem(), sourcePath);
    }

    static Value wrapPromise(Context context, CompletableFuture<?> javaFuture) {
        var global = context.getBindings("js");
        var promiseConstructor = promiseConstructorCache.get(context);
        if (promiseConstructor == null) {
            promiseConstructor = global.getMember("Promise");
            promiseConstructorCache.put(context, promiseConstructor);
        }
        return promiseConstructor.newInstance((ProxyExecutable) arguments -> {
            Value resolve = arguments[0];
            Value reject = arguments[1];
            javaFuture.whenComplete((result, ex) -> {
                if (result != PROMISE_FAILED) {
                    resolve.execute(result);
                } else {
                    reject.execute(ex);
                }
            });
            return null;
        });
    }
}
