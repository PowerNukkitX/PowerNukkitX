package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerMoveEvent extends PlayerEvent implements Cancellable {

    public enum Type {
        POSITION_CHANGE,
        ROTATE,
        ALL
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Location from;
    private Location to;
    private boolean resetBlocksAround;
    private Type movementType;

    public PlayerMoveEvent(Player player, Location from, Location to) {
        this(player, from, to, true);
    }

    public PlayerMoveEvent(Player player, Location from, Location to, boolean resetBlocks) {
        this(player, from, to, resetBlocks, Type.ALL);
    }

    public PlayerMoveEvent(Player player, Location from, Location to, boolean resetBlocks, Type movementType) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.resetBlocksAround = resetBlocks;
        this.movementType = movementType;
    }

    @Override
    public void setCancelled() {
        super.setCancelled();
    }
}
