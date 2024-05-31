package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class BlockHarvestEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Block newState;
    private Item[] drops;
    /**
     * @deprecated 
     */
    

    public BlockHarvestEvent(Block block, Block newState, Item[] drops) {
        super(block);
        this.newState = newState;
        this.drops = drops;
    }

    public Block getNewState() {
        return newState;
    }
    /**
     * @deprecated 
     */
    

    public void setNewState(Block newState) {
        this.newState = newState;
    }

    public Item[] getDrops() {
        return drops;
    }
    /**
     * @deprecated 
     */
    

    public void setDrops(Item[] drops) {
        this.drops = drops;
    }

}
