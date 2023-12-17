package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;


public class BlockNetherSprout extends BlockRoots {


    public BlockNetherSprout() {
    }

    @Override
    public int getId() {
        return NETHER_SPROUTS_BLOCK;
    }

    @Override
    public String getName() {
        return "Nether Sprouts Block";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.NETHER_SPROUTS);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{ toItem() };
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }
}
