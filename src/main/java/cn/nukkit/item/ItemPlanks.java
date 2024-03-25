package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemPlanks extends Item {
    public ItemPlanks() {
        this(0, 1);
    }

    public ItemPlanks(Integer meta) {
        this(meta, 1);
    }

    public ItemPlanks(Integer meta, int count) {
        super(PLANKS, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0:
                this.name = "Oak Planks";
                setBlockUnsafe(Block.get(BlockID.OAK_PLANKS));
            case 1:
                this.name = "Spruce Planks";
                setBlockUnsafe(Block.get(BlockID.SPRUCE_PLANKS));
            case 2:
                this.name = "Birch Planks";
                setBlockUnsafe(Block.get(BlockID.BIRCH_PLANKS));
            case 3:
                this.name = "Jungle Planks";
                setBlockUnsafe(Block.get(BlockID.JUNGLE_PLANKS));
            case 4:
                this.name = "Acacia Planks";
                setBlockUnsafe(Block.get(BlockID.ACACIA_PLANKS));
            case 5:
                this.name = "Dark Oak Planks";
                setBlockUnsafe(Block.get(BlockID.DARK_OAK_PLANKS));
        }
        this.meta = 0;
    }
}