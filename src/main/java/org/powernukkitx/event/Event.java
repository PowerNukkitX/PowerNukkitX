package org.powernukkitx.event;

import org.powernukkitx.utils.EventException;

/**
 * Describes things that happens in the server.
 *
 * <p>Things that happens in the server is called a <b>event</b>. Define a procedure that should be executed
 * when a event happens, this procedure is called a <b>listener</b>.</p>
 *
 * <p>When Nukkit is calling a handler, the event needed to listen is judged by the type of the parameter. </p>
 *
 * <p>For the way to implement a listener, see: {@link org.powernukkitx.event.Listener}</p>
 *
 * @author Unknown(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @see org.powernukkitx.event.EventHandler
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
}
