package cn.nukkit.plugin.js.external;

import cn.nukkit.plugin.js.JSExternal;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public final class ExternalFunction extends JSExternal implements ProxyExecutable {
    public ExternalFunction(Context sourceContext, Value value) {
        super(sourceContext, value);
    }

    @Override
    public Object execute(Value... arguments) {
        synchronized (sourceContext) {
            checkAlive();
            if(value.canExecute())
                return value.execute((Object[]) arguments);
            return null;
        }
    }
}
