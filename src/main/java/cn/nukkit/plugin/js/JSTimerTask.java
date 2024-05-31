package cn.nukkit.plugin.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.TimerTask;

public final class JSTimerTask extends TimerTask {
    private final long id;
    private final Context jsContext;
    private final Value callback;
    private final Object[] args;
    /**
     * @deprecated 
     */
    

    public JSTimerTask(long id, Context jsContext, Value callback, Object... args) {
        this.id = id;
        this.jsContext = jsContext;
        this.callback = callback;
        this.args = args;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void run() {
        synchronized (jsContext) {
            if (callback != null && callback.canExecute()) {
                callback.executeVoid(args);
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public long getId() {
        return id;
    }
}
