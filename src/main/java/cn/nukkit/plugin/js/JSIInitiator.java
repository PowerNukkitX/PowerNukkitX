package cn.nukkit.plugin.js;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class JSIInitiator {
    private static final Value NULL = Value.asValue(null);

    public static final Timer jsTimer = new Timer();
    public static final Long2ObjectMap<JSTimerTask> jsTimeTaskMap = new Long2ObjectOpenHashMap<>();
    public static final Map<Context, LongList> contextTimerIdMap = new ConcurrentHashMap<>();
    private static final AtomicLong globalTimerId = new AtomicLong();

    public static void init(Context context) {
        initTimer(context);
    }

    public static void closeContext(Context context) {
        if (contextTimerIdMap.containsKey(context)) {
            for (long i : contextTimerIdMap.get(context)) {
                jsTimeTaskMap.remove(i).cancel();
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static void initTimer(Context context) {
        var global = context.getBindings("js");
        global.putMember("setInterval", (ProxyExecutable) arguments -> {
            var len = arguments.length;
            if (len == 0) {
                throw new IllegalArgumentException("Failed to execute 'setInterval': 1 argument required, but only 0 present.");
            }
            var args = new Object[len - 1];
            if (args.length != 0) {
                System.arraycopy(arguments, 1, args, 0, len - 1);
            }
            var id = globalTimerId.getAndIncrement();
            var task = new JSTimerTask(id, context, arguments[0], args);
            var interval = 0L;
            if (len != 1) {
                var tmp = arguments[1];
                if (tmp.isNumber()) {
                    interval = tmp.asLong();
                }
            }
            synchronized (jsTimeTaskMap) {
                jsTimeTaskMap.put(id, task);
            }
            addContextTimerId(context, id);
            jsTimer.scheduleAtFixedRate(task, interval, interval);
            return Value.asValue(id);
        });
        global.putMember("setTimeout", (ProxyExecutable) arguments -> {
            var len = arguments.length;
            if (len == 0) {
                throw new IllegalArgumentException("Failed to execute 'setTimeout': 1 argument required, but only 0 present.");
            }
            var args = new Object[len - 1];
            if (args.length != 0) {
                System.arraycopy(arguments, 1, args, 0, len - 1);
            }
            var id = globalTimerId.getAndIncrement();
            var task = new JSTimerTask(id, context, arguments[0], args);
            var timeout = 0L;
            if (len != 1) {
                var tmp = arguments[1];
                if (tmp.isNumber()) {
                    timeout = tmp.asLong();
                }
            }
            synchronized (jsTimeTaskMap) {
                jsTimeTaskMap.put(id, task);
            }
            addContextTimerId(context, id);
            jsTimer.schedule(task, timeout);
            return Value.asValue(id);
        });
        global.putMember("clearInterval", (ProxyExecutable) arguments -> {
            if (arguments.length > 0) {
                var tmp = arguments[0];
                if (tmp.isNumber()) {
                    removeContextTimerId(context, tmp.asLong());
                }
            }
            return NULL;
        });
        global.putMember("clearTimeout", (ProxyExecutable) arguments -> {
            if (arguments.length > 0) {
                var tmp = arguments[0];
                if (tmp.isNumber()) {
                    removeContextTimerId(context, tmp.asLong());
                }
            }
            return NULL;
        });
    }

    private static void addContextTimerId(Context context, long timerId) {
        var idList = contextTimerIdMap.get(context);
        if (idList != null) {
            idList.add(timerId);
        } else {
            idList = new LongArrayList();
            idList.add(timerId);
            contextTimerIdMap.put(context, idList);
        }
    }

    private static void removeContextTimerId(Context context, long timerId) {
        synchronized (jsTimeTaskMap) {
            jsTimeTaskMap.remove(timerId).cancel();
        }
        var idList = contextTimerIdMap.get(context);
        if (idList != null) {
            idList.rem(timerId);
        }
    }
}
