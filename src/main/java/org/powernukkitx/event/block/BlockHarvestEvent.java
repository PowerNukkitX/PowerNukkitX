package org.powernukkitx.event.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;

public class BlockHarvestEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Block newState;
    private Item[] drops;

    public BlockHarvestEvent(Block block, Block newState, Item[] drops) {
        super(block);
        this.newState = newState;
        this.drops = drops;
    }

    public Block getNewState() {
        return newState;
    }

    public void setNewState(Block newState) {
        this.newState = newState;
    }

    public Item[] getDrops() {
        return drops;
    }

    public void setDrops(Item[] drops) {
        this.drops = drops;
    }

}
