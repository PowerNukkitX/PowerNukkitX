package org.powernukkitx.plugin;

import org.powernukkitx.event.Event;
import org.powernukkitx.event.Listener;
import org.powernukkitx.utils.EventException;

/**
 * @author iNevet (Nukkit Project)
 */
public interface EventExecutor {

    void execute(Listener listener, Event event) throws EventException;
}
