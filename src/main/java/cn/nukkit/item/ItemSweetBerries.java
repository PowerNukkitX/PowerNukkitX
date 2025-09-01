package cn.nukkit.item;

import cn.nukkit.block.BlockSweetBerryBush;

public class ItemSweetBerries extends ItemFood {

    public ItemSweetBerries() {
        this(0, 1);
    }

    public ItemSweetBerries(Integer meta) {
        this(meta, 1);
    }

    public ItemSweetBerries(Integer meta, int count) {
        super(SWEET_BERRIES, meta, count, "Sweet Berries");
        this.block = new BlockSweetBerryBush();
    }

    @Override
    public int getNutrition() {
        return 2;
    }

    @Override
    public float getSaturation() {
        return 0.4F;
    }
}
