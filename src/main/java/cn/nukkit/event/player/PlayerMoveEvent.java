package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Location;

public class PlayerMoveEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Location from;
    private Location to;

    private boolean resetBlocksAround;
    /**
     * @deprecated 
     */
    

    public PlayerMoveEvent(Player player, Location from, Location to) {
        this(player, from, to, true);
    }
    /**
     * @deprecated 
     */
    

    public PlayerMoveEvent(Player player, Location from, Location to, boolean resetBlocks) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.resetBlocksAround = resetBlocks;
    }

    public Location getFrom() {
        return from;
    }
    /**
     * @deprecated 
     */
    

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }
    /**
     * @deprecated 
     */
    

    public void setTo(Location to) {
        this.to = to;
    }
    /**
     * @deprecated 
     */
    

    public boolean isResetBlocksAround() {
        return resetBlocksAround;
    }
    /**
     * @deprecated 
     */
    

    public void setResetBlocksAround(boolean value) {
        this.resetBlocksAround = value;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setCancelled() {
        super.setCancelled();
    }
}
