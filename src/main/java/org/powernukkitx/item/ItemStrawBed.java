package org.powernukkitx.item;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockStrawBed;

/**
 * The straw bed item.
 */
public class ItemStrawBed extends Item {
    public ItemStrawBed() {
        this(0, 1);
    }

    public ItemStrawBed(Integer meta) {
        this(meta, 1);
    }

    public ItemStrawBed(Integer meta, int count) {
        super(BlockID.STRAW_BED, meta, count);
    }

    @Override
    public void internalAdjust() {
        name = "Straw Bed";
        block = BlockStrawBed.PROPERTIES.getDefaultState().toBlock();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
