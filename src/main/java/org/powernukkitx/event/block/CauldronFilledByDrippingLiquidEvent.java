package org.powernukkitx.event.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.property.enums.CauldronLiquid;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

public class CauldronFilledByDrippingLiquidEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private CauldronLiquid liquid;

    private int liquidLevelIncrement;

    public CauldronFilledByDrippingLiquidEvent(Block cauldron, CauldronLiquid liquid, int liquidLevelIncrement) {
        super(cauldron);
        this.liquid = liquid;
        this.liquidLevelIncrement = liquidLevelIncrement;
    }

    public CauldronLiquid getLiquid() {
        return liquid;
    }

    public void setLiquid(CauldronLiquid liquid) {
        this.liquid = liquid;
    }

    public int getLiquidLevelIncrement() {
        return liquidLevelIncrement;
    }

    public void setLiquidLevelIncrement(int liquidLevelIncrement) {
        this.liquidLevelIncrement = liquidLevelIncrement;
    }

}
