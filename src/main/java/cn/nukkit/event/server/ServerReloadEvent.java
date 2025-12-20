package cn.nukkit.event.server;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Triggers before server is reloaded.
 *
 * @author xRookieFight
 * @since 20/12/2025
 */
public class ServerReloadEvent extends ServerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
}
