package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

/**
 * @author CreeperFace
 * @since 12.5.2017
 */
public class BlockRedstoneEvent extends BlockEvent {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int oldPower;
    private int newPower;
    /**
     * @deprecated 
     */
    

    public BlockRedstoneEvent(Block block, int oldPower, int newPower) {
        super(block);
        this.oldPower = oldPower;
        this.newPower = newPower;
    }
    /**
     * @deprecated 
     */
    

    public int getOldPower() {
        return oldPower;
    }
    /**
     * @deprecated 
     */
    

    public int getNewPower() {
        return newPower;
    }
}
