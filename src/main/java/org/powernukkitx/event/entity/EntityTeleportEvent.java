package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.event.player.PlayerTeleportEvent;
import org.powernukkitx.level.Location;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityTeleportEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Location from;
    private Location to;
    private final PlayerTeleportEvent.TeleportCause cause;

    public EntityTeleportEvent(Entity entity, Location from, Location to) {
        this(entity, from, to, PlayerTeleportEvent.TeleportCause.UNKNOWN);
    }

    public EntityTeleportEvent(Entity entity, Location from, Location to, PlayerTeleportEvent.TeleportCause cause) {
        this.entity = entity;
        this.from = from;
        this.to = to;
        this.cause = cause;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public PlayerTeleportEvent.TeleportCause getCause() {
        return cause;
    }
}
