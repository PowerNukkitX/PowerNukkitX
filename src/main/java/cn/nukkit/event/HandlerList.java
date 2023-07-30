package cn.nukkit.event;

import cn.nukkit.plugin.Plugin;
import java.util.*;
import lombok.Getter;

/**
 * @author Nukkit Team.
 */
public class HandlerList {

    private volatile RegisteredListener[] handlers = null;

    @Getter
    private final EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerSlots;

    private final String eventName;

    public HandlerList(String eventName) {
        this.eventName = eventName;
        this.handlerSlots = new EnumMap<>(EventPriority.class);
        for (EventPriority priority : EventPriority.values()) {
            handlerSlots.put(priority, new ArrayList<>());
        }
    }

    public synchronized void register(RegisteredListener listener) {
        if (handlerSlots.get(listener.getPriority()).contains(listener))
            throw new IllegalStateException("This listener is already registered to priority "
                    + listener.getPriority().toString() + " of event " + eventName);
        handlers = null;
        handlerSlots.get(listener.getPriority()).add(listener);
    }

    public void registerAll(Collection<RegisteredListener> listeners) {
        for (RegisteredListener listener : listeners) {
            register(listener);
        }
    }

    public synchronized void unregister(RegisteredListener listener) {
        if (handlerSlots.get(listener.getPriority()).remove(listener)) {
            handlers = null;
        }
    }

    public synchronized void unregister(Listener listener) {
        boolean changed = false;
        for (List<RegisteredListener> list : handlerSlots.values()) {
            for (ListIterator<RegisteredListener> i = list.listIterator(); i.hasNext(); ) {
                if (i.next().getListener().equals(listener)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) handlers = null;
    }

    public synchronized void unregister(Plugin plugin) {
        boolean changed = false;
        for (List<RegisteredListener> list : handlerSlots.values()) {
            for (ListIterator<RegisteredListener> iterator = list.listIterator(); iterator.hasNext(); ) {
                if (iterator.next().getPlugin().equals(plugin)) {
                    iterator.remove();
                    changed = true;
                }
            }
        }
        if (changed) handlers = null;
    }

    public void clear() {
        for (EventPriority priority : EventPriority.values()) {
            handlerSlots.put(priority, new ArrayList<>());
        }
        handlers = RegisteredListener.EMPTY_ARRAY;
    }

    public synchronized void bake() {
        if (handlers != null) return; // Don't re-bake when still valid
        List<RegisteredListener> entries = new ArrayList<>();
        for (Map.Entry<EventPriority, ArrayList<RegisteredListener>> entry : handlerSlots.entrySet()) {
            entries.addAll(entry.getValue());
        }
        handlers = entries.toArray(RegisteredListener.EMPTY_ARRAY);
    }

    public RegisteredListener[] getListenerList() {
        RegisteredListener[] handlers;
        while ((handlers = this.handlers) == null) {
            bake();
        } // This prevents fringe cases of returning null
        return handlers;
    }

    public List<RegisteredListener> getListenersByPriority(EventPriority priority) {
        return handlerSlots.get(priority);
    }

    public boolean isEmpty() {
        RegisteredListener[] handlers = this.handlers;
        if (handlers != null) {
            return handlers.length == 0;
        }
        return getListenerList().length == 0;
    }
}
