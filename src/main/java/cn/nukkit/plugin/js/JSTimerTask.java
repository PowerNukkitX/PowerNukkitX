package cn.nukkit.plugin.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.TimerTask;

public final class JSTimerTask extends TimerTask {
    private final long id;
    private final Context jsContext;
    private final Value callback;
    private final Object[] args;

    public JSTimerTask(long id, Context jsContext, Value callback, Object... args) {
        this.id = id;
        this.jsContext = jsContext;
        this.callback = callback;
        this.args = args;
    }

    @Override
    public void run() {
        synchronized (jsContext) {
            if (callback != null && callback.canExecute()) {
                callback.executeVoid(args);
            }
        }
    }

    public long getId() {
        return id;
    }
}
