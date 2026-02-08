package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Location;
import lombok.Getter;
import lombok.Setter;

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

    @Setter @Getter
    private Location from;
    @Setter @Getter
    private Location to;

    @Setter @Getter
    private boolean resetBlocksAround;

    @Setter @Getter
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
