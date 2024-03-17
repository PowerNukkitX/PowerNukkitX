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
        adjust();
    }

    public void adjust() {
        switch (getDamage()) {
            case 0 -> {
                name = "Acacia Leaves";
                block = Block.get(BlockID.ACACIA_LEAVES);
            }
            case 1 -> {
                name = "Dark Oak Leaves";
                block = Block.get(BlockID.DARK_OAK_LEAVES);
            }
            default -> throw new IllegalArgumentException("Invalid damage: " + getDamage());
        }
    }
}