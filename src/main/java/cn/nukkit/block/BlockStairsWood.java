package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * @author xtypr
 * @since 2015/11/25
 */
public abstract class BlockStairsWood extends BlockStairs {
    public BlockStairsWood(BlockState blockState) {
        super(blockState);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public Item[] getDrops(Item item) {
         return new Item[]{
            toItem()
            };
    }
}
