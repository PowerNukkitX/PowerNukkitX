package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;

public class ItemMelonSeeds extends Item {
    public ItemMelonSeeds() {
        this(0, 1);
    }

    public ItemMelonSeeds(Integer meta) {
        this(meta, 1);
    }

    public ItemMelonSeeds(Integer meta, int count) {
        super(MELON_SEEDS, 0, count, "Melon Seeds");
        this.block = Block.get(BlockID.MELON_STEM);
    }
}