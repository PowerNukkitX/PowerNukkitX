package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockPlanksMangrove extends BlockSolid {
    public BlockPlanksMangrove() {
    }

    @Override
    public int getId() {
        return MANGROVE_PLANKS;
    }

    @Override
    public String getName() {
        return "Mangrove Planks";
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

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWNISH_RED;
    }
}
