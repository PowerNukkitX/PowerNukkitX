package cn.nukkit.event.server;

import cn.nukkit.event.HandlerList;

public class ServerStartedEvent extends ServerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
}
