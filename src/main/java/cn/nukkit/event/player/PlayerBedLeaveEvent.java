package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

public class PlayerBedLeaveEvent extends PlayerEvent {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block bed;
    /**
     * @deprecated 
     */
    

    public PlayerBedLeaveEvent(Player player, Block bed) {
        this.player = player;
        this.bed = bed;
    }

    public Block getBed() {
        return bed;
    }
}
