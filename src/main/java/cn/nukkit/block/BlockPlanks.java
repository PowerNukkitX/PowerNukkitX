package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockPlanks extends BlockSolid {

    public BlockPlanks(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
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
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
