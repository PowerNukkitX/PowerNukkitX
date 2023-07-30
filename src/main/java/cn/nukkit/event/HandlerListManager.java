package cn.nukkit.event;

import cn.nukkit.plugin.Plugin;
import java.util.HashMap;

public class HandlerListManager {

    private static HandlerListManager instance = null;

    private static final HashMap<String, HandlerList> allLists = new HashMap<>();

    public static HandlerListManager global() {
        return instance != null ? instance : (instance = new HandlerListManager());
    }

    public void unregisterAll() {
        synchronized (allLists) {
            for (HandlerList handler : allLists.values()) {
                handler.clear();
            }
        }
    }

    public void unregisterAll(Plugin plugin) {
        synchronized (allLists) {
            for (HandlerList handler : allLists.values()) {
                handler.unregister(plugin);
            }
        }
    }

    public void unregisterAll(Listener listener) {
        synchronized (allLists) {
            for (HandlerList handler : allLists.values()) {
                handler.unregister(listener);
            }
        }
    }

    public HandlerList getListFor(Class<? extends Event> event) {
        return getListFor(event.getName());
    }

    public HandlerList getListFor(String event) {
        if (allLists.containsKey(event)) {
            return allLists.get(event);
        }
        HandlerList handler = new HandlerList(event);
        allLists.put(event, handler);
        return handler;
    }

    public HashMap<String, HandlerList> getAll() {
        return allLists;
    }
}
