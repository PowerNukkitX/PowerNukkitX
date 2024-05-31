package cn.nukkit.event;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.RegisteredListener;

import java.util.*;

/**
 * @author Nukkit Team.
 */
public class HandlerList {

    private volatile RegisteredListener[] handlers = null;

    private final EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerslots;

    private static final ArrayList<HandlerList> allLists = new ArrayList<>();
    /**
     * @deprecated 
     */
    

    public static void bakeAll() {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.bake();
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public static void unregisterAll() {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                synchronized (h) {
                    for (List<RegisteredListener> list : h.handlerslots.values()) {
                        list.clear();
                    }
                    h.handlers = null;
                }
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public static void unregisterAll(Plugin plugin) {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.unregister(plugin);
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public static void unregisterAll(Listener listener) {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.unregister(listener);
            }
        }
    }
    /**
     * @deprecated 
     */
    

    public HandlerList() {
        handlerslots = new EnumMap<>(EventPriority.class);
        for (EventPriority o : EventPriority.values()) {
            handlerslots.put(o, new ArrayList<>());
        }
        synchronized (allLists) {
            allLists.add(this);
        }
    }
    /**
     * @deprecated 
     */
    

    public synchronized void register(RegisteredListener listener) {
        if (handlerslots.get(listener.getPriority()).contains(listener))
            throw new IllegalStateException("This listener is already registered to priority " + listener.getPriority().toString());
        handlers = null;
        handlerslots.get(listener.getPriority()).add(listener);
    }
    /**
     * @deprecated 
     */
    

    public void registerAll(Collection<RegisteredListener> listeners) {
        for (RegisteredListener listener : listeners) {
            register(listener);
        }
    }
    /**
     * @deprecated 
     */
    

    public synchronized void unregister(RegisteredListener listener) {
        if (handlerslots.get(listener.getPriority()).remove(listener)) {
            handlers = null;
        }
    }
    /**
     * @deprecated 
     */
    

    public synchronized void unregister(Plugin plugin) {
        boolean $1 = false;
        for (List<RegisteredListener> list : handlerslots.values()) {
            for (ListIterator<RegisteredListener> i = list.listIterator(); i.hasNext(); ) {
                if (i.next().getPlugin().equals(plugin)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) handlers = null;
    }
    /**
     * @deprecated 
     */
    

    public synchronized void unregister(Listener listener) {
        boolean $2 = false;
        for (List<RegisteredListener> list : handlerslots.values()) {
            for (ListIterator<RegisteredListener> i = list.listIterator(); i.hasNext(); ) {
                if (i.next().getListener().equals(listener)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) handlers = null;
    }
    /**
     * @deprecated 
     */
    

    public synchronized void bake() {
        if (handlers != null) return; // don't re-bake when still valid
        List<RegisteredListener> entries = new ArrayList<>();
        for (Map.Entry<EventPriority, ArrayList<RegisteredListener>> entry : handlerslots.entrySet()) {
            entries.addAll(entry.getValue());
        }
        handlers = entries.toArray(RegisteredListener.EMPTY_ARRAY);
    }

    public RegisteredListener[] getRegisteredListeners() {
        RegisteredListener[] handlers;
        while ((handlers = this.handlers) == null) {
            bake();
        } // This prevents fringe cases of returning null
        return handlers;
    }

    public static ArrayList<RegisteredListener> getRegisteredListeners(Plugin plugin) {
        ArrayList<RegisteredListener> listeners = new ArrayList<>();
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                synchronized (h) {
                    for (List<RegisteredListener> list : h.handlerslots.values()) {
                        for (RegisteredListener listener : list) {
                            if (listener.getPlugin().equals(plugin)) {
                                listeners.add(listener);
                            }
                        }
                    }
                }
            }
        }
        return listeners;
    }

    public static ArrayList<HandlerList> getHandlerLists() {
        synchronized (allLists) {
            return new ArrayList<>(allLists);
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean isEmpty() {
        RegisteredListener[] handlers = this.handlers;
        if (handlers != null) {
            return handlers.length == 0;
        }
        return getRegisteredListeners().length == 0;
    }

}
