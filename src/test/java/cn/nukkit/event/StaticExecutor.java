package cn.nukkit.event;

import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.EventExecutor;
import cn.nukkit.utils.EventException;

public class StaticExecutor implements EventExecutor {
    @Override
    public void execute(Listener listener, Event event) throws EventException {
        ((EventCallTest) (listener)).handle((PlayerJoinEvent) event);
    }
}
