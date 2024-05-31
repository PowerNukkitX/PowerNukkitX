package cn.nukkit.event.inventory;

import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.block.BlockEvent;
import cn.nukkit.item.Item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class FurnaceBurnEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockEntityFurnace furnace;
    private final Item fuel;
    private int burnTime;
    private boolean $2 = true;
    /**
     * @deprecated 
     */
    

    public FurnaceBurnEvent(BlockEntityFurnace furnace, Item fuel, int burnTime) {
        super(furnace.getBlock());
        this.fuel = fuel;
        this.burnTime = burnTime;
        this.furnace = furnace;
    }

    public BlockEntityFurnace getFurnace() {
        return furnace;
    }

    public Item getFuel() {
        return fuel;
    }
    /**
     * @deprecated 
     */
    

    public int getBurnTime() {
        return burnTime;
    }
    /**
     * @deprecated 
     */
    

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }
    /**
     * @deprecated 
     */
    

    public boolean isBurning() {
        return burning;
    }
    /**
     * @deprecated 
     */
    

    public void setBurning(boolean burning) {
        this.burning = burning;
    }
}
