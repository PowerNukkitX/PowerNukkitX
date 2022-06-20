package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockproperty.value.CauldronLiquid;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class CauldronFilledByDrippingLiquidEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private CauldronLiquid liquid;

    private int liquidLevelIncrement;

    public CauldronFilledByDrippingLiquidEvent(Block cauldron, CauldronLiquid liquid,int liquidLevelIncrement) {
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

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }
}
