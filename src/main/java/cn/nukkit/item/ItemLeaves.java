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
        adjust();
    }

    public void adjust() {
        switch (getDamage()) {
            case 0 -> {
                name = "Oak Leaves";
                block = Block.get(BlockID.OAK_LEAVES);
            }
            case 1 -> {
                name = "Spruce Leaves";
                block = Block.get(BlockID.SPRUCE_LEAVES);
            }
            case 2 -> {
                name = "Birch Leaves";
                block = Block.get(BlockID.BIRCH_LEAVES);
            }
            case 3 -> {
                name = "Jungle Leaves";
                block = Block.get(BlockID.JUNGLE_LEAVES);
            }
            default -> throw new IllegalArgumentException("Invalid damage: " + getDamage());
        }
    }
}