package cn.nukkit.event.inventory;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.block.BlockEvent;
import cn.nukkit.item.Item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class FurnaceSmeltEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockEntityFurnace furnace;
    private final Item source;
    private Item result;
    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    private float xp;

    public FurnaceSmeltEvent(BlockEntityFurnace furnace, Item source, Item result, float xp) {
        super(furnace.getBlock());
        this.source = source.clone();
        this.source.setCount(1);
        this.result = result;
        this.furnace = furnace;
        this.xp = xp;
    }

    public BlockEntityFurnace getFurnace() {
        return furnace;
    }

    public Item getSource() {
        return source;
    }

    public Item getResult() {
        return result;
    }

    public void setResult(Item result) {
        this.result = result;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }
}
