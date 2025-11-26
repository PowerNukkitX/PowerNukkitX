package cn.nukkit.event;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.EventException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        // Ensure a server instance is available
        Server server = Server.getInstance();
        if (server == null) {
            throw new EventException("Server instance is null while calling event " + getEventName());
        }

        // Ensure the plugin manager is present
        PluginManager pluginManager = server.getPluginManager();
        if (pluginManager == null) {
            throw new EventException("PluginManager is null while calling event " + getEventName());
        }

        // Dispatch the event and handle any throwable raised by handlers.
        try {
            pluginManager.callEvent(this);
        } catch (Throwable t) {
            // Primary logging via server logger; fallback to java.util.logging
            try {
                Objects.requireNonNull(server.getLogger()).error("Exception while calling event " + getEventName(), t);
            } catch (Throwable ignored) {
                Logger.getLogger(Event.class.getName()).log(Level.SEVERE, "Exception while calling event " + getEventName(), t);
            }

            // Wrap and rethrow so callers receive a consistent exception type
            throw new EventException(t, "Exception while calling event " + getEventName());
        }
    }
}
