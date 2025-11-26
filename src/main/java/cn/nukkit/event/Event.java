package cn.nukkit.event;

import cn.nukkit.utils.EventException;

/**
 * Base class for events that occur in the server.
 *
 * <p>An Event represents something that happens in the server and can be
 * listened to by plugins via {@link Listener} methods annotated with
 * {@link EventHandler}.</p>
 *
 * <p>Use {@link #call()} to dispatch the event to the plugin manager. Subclasses
 * may provide additional data and behaviour (for example implement
 * {@link Cancellable}).</p>
 *
 * @see cn.nukkit.event.EventHandler
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public abstract class Event {

    protected String eventName = null;
    private boolean isCancelled = false;

    final public String getEventName() {
        return eventName == null ? getClass().getName() : eventName;
    }

    public boolean isCancelled() {
        if (!(this instanceof Cancellable)) {
            throw new EventException("Event is not Cancellable");
        }
        return isCancelled;
    }

    public void setCancelled() {
        setCancelled(true);
    }

    public void setCancelled(boolean value) {
        if (!(this instanceof Cancellable)) {
            throw new EventException("Event is not Cancellable");
        }
        isCancelled = value;
    }

    public void call() {
        EventDispatcher.call(this);
    }
}
