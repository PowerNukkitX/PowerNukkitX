package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemTorchflowerSeeds extends Item {

    public ItemTorchflowerSeeds() {
        this(0, 1);
    }

    public ItemTorchflowerSeeds(Integer meta) {
        this(meta, 1);
    }

    public ItemTorchflowerSeeds(Integer meta, int count) {
        super(TORCHFLOWER_SEEDS, meta, count, "Torchflower Seeds");
        this.block = Block.get(BlockID.TORCHFLOWER_CROP);
    }
}
