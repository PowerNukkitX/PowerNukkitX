package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockBambooPlanks extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooPlanks(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Bamboo Planks";
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