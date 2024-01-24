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
        adjust();
    }

    public void adjust() {
        switch (getDamage()) {
            case 0:
                this.name = "Oak Planks";
                this.block = Block.get(BlockID.OAK_PLANKS);
                return;
            case 1:
                this.name = "Spruce Planks";
                this.block = Block.get(BlockID.SPRUCE_PLANKS);
                return;
            case 2:
                this.name = "Birch Planks";
                this.block = Block.get(BlockID.BIRCH_PLANKS);
                return;
            case 3:
                this.name = "Jungle Planks";
                this.block = Block.get(BlockID.JUNGLE_PLANKS);
                return;
            case 4:
                this.name = "Acacia Planks";
                this.block = Block.get(BlockID.ACACIA_PLANKS);
                return;
            case 5:
                this.name = "Dark Oak Planks";
                this.block = Block.get(BlockID.DARK_OAK_PLANKS);
        }
    }
}