package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

@PowerNukkitOnly
public class BlockHarvestEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Block newState;
    private Item[] drops;

    @PowerNukkitOnly
    public BlockHarvestEvent(Block block, Block newState, Item[] drops) {
        super(block);
        this.newState = newState;
        this.drops = drops;
    }

    @PowerNukkitOnly
    public Block getNewState() {
        return newState;
    }

    @PowerNukkitOnly
    public void setNewState(Block newState) {
        this.newState = newState;
    }

    @PowerNukkitOnly
    public Item[] getDrops() {
        return drops;
    }

    @PowerNukkitOnly
    public void setDrops(Item[] drops) {
        this.drops = drops;
    }

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }

}
