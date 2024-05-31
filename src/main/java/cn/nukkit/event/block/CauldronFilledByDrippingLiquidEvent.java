package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.property.enums.CauldronLiquid;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class CauldronFilledByDrippingLiquidEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private CauldronLiquid liquid;

    private int liquidLevelIncrement;
    /**
     * @deprecated 
     */
    

    public CauldronFilledByDrippingLiquidEvent(Block cauldron, CauldronLiquid liquid, int liquidLevelIncrement) {
        super(cauldron);
        this.liquid = liquid;
        this.liquidLevelIncrement = liquidLevelIncrement;
    }

    public CauldronLiquid getLiquid() {
        return liquid;
    }
    /**
     * @deprecated 
     */
    

    public void setLiquid(CauldronLiquid liquid) {
        this.liquid = liquid;
    }
    /**
     * @deprecated 
     */
    

    public int getLiquidLevelIncrement() {
        return liquidLevelIncrement;
    }
    /**
     * @deprecated 
     */
    

    public void setLiquidLevelIncrement(int liquidLevelIncrement) {
        this.liquidLevelIncrement = liquidLevelIncrement;
    }

}
