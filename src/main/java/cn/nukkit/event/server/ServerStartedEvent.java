package cn.nukkit.event.server;

import cn.nukkit.event.HandlerList;


/**
 * 服务器启动完毕后会触发，注意reload也会触发
 */
public class ServerStartedEvent extends ServerEvent {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
}
