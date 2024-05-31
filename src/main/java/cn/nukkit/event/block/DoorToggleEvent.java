package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

/**
 * @author Snake1999
 * @since 2016/1/22
 */
public class DoorToggleEvent extends BlockUpdateEvent {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Player player;
    /**
     * @deprecated 
     */
    

    public DoorToggleEvent(Block block, Player player) {
        super(block);
        this.player = player;
    }
    /**
     * @deprecated 
     */
    

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
