package cn.nukkit.plugin.js;

import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.CommonJSPlugin;
import cn.nukkit.plugin.EventExecutor;
import cn.nukkit.utils.EventException;
import org.graalvm.polyglot.Value;

public final class JSEventManager {
    private final CommonJSPlugin jsPlugin;

    public JSEventManager(CommonJSPlugin jsPlugin) {
        this.jsPlugin = jsPlugin;
    }

    @SuppressWarnings("unchecked")
    public boolean register(String fullEventName, EventPriority priority, Value callback) {
        if (callback.canExecute()) {
            try {
                Server.getInstance().getPluginManager().registerEvent((Class<? extends Event>) Class.forName(fullEventName), jsPlugin, priority, (listener, event) -> callback.executeVoid(event), jsPlugin);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return false;
    }
}
