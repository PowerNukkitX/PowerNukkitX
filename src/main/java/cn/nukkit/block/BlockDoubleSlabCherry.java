package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockDoubleSlabCherry extends BlockDoubleSlabBase {


    public BlockDoubleSlabCherry() {
        this(0);
    }


    protected BlockDoubleSlabCherry(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getSlabName() {
        return "Double Cherry Slab";
    }

    @Override
    public int getId() {
        return DOUBLE_CHERRY_SLAB;
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return BlockSlab.SIMPLE_SLAB_PROPERTIES;
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
    public int getSingleSlabId() {
        return CHERRY_SLAB;
    }
}
