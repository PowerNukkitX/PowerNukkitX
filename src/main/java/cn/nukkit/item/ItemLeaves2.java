package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemLeaves2 extends Item {
    public ItemLeaves2() {
        this(0, 1);
    }

    public ItemLeaves2(Integer meta) {
        this(meta, 1);
    }

    public ItemLeaves2(Integer meta, int count) {
        super(LEAVES2, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0 -> {
                name = "Acacia Leaves";
                setBlockUnsafe(Block.get(BlockID.ACACIA_LEAVES));
                this.meta = 0;
            }
            case 1 -> {
                name = "Dark Oak Leaves";
                setBlockUnsafe(Block.get(BlockID.DARK_OAK_LEAVES));
                this.meta = 0;
            }
            default -> throw new IllegalArgumentException("Invalid damage: " + getDamage());
        }
    }
}