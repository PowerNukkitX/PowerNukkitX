package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemLeaves extends Item {

    public ItemLeaves() {
        this(0, 1);
    }

    public ItemLeaves(Integer meta) {
        this(meta, 1);
    }

    public ItemLeaves(Integer meta, int count) {
        super(LEAVES, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0 -> {
                name = "Oak Leaves";
                setBlockUnsafe(Block.get(BlockID.OAK_LEAVES));
            }
            case 1 -> {
                name = "Spruce Leaves";
                setBlockUnsafe(Block.get(BlockID.SPRUCE_LEAVES));
                this.meta = 0;
            }
            case 2 -> {
                name = "Birch Leaves";
                setBlockUnsafe(Block.get(BlockID.BIRCH_LEAVES));
                this.meta = 0;
            }
            case 3 -> {
                name = "Jungle Leaves";
                setBlockUnsafe(Block.get(BlockID.JUNGLE_LEAVES));
                this.meta = 0;
            }
            default -> throw new IllegalArgumentException("Invalid damage: " + getDamage());
        }
    }
}