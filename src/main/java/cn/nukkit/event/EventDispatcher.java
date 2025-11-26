package cn.nukkit.event;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.EventException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central dispatcher for events. Replaces direct uses of PluginManager.callEvent(...) to provide
 * consistent null checks, logging and exception handling in a single place.
 */
public final class EventDispatcher {

    private EventDispatcher() {
        // Utility
    }

    public static void call(Event event) {
        if (event == null) {
            throw new EventException("Event is null");
        }

        Server server = Server.getInstance();
        if (server == null) {
            throw new EventException("Server instance is null while calling event " + event.getEventName());
        }

        PluginManager pluginManager = server.getPluginManager();
        if (pluginManager == null) {
            throw new EventException("PluginManager is null while calling event " + event.getEventName());
        }

        try {
            pluginManager.callEvent(event);
        } catch (Throwable t) {
            try {
                Objects.requireNonNull(server.getLogger()).error("Exception while calling event " + event.getEventName(), t);
            } catch (Throwable ignored) {
                Logger.getLogger(EventDispatcher.class.getName()).log(Level.SEVERE, "Exception while calling event " + event.getEventName(), t);
            }
            throw new EventException(t, "Exception while calling event " + event.getEventName());
        }
    }
}

