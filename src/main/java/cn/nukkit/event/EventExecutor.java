package cn.nukkit.event;

import cn.nukkit.utils.EventException;

/**
 * @author iNevet (Nukkit Project)
 */
public interface EventExecutor {

    void execute(Listener listener, Event event) throws EventException;
}
