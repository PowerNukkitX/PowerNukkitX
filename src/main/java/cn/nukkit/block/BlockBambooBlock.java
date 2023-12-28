package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.*;


public class BlockBambooBlock extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_BLOCK, PILLAR_AXIS);

    public BlockBambooBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooBlock(BlockState blockState) {
        super(blockState);
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Bamboo Block";
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
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedBambooBlock.PROPERTIES.getDefaultState();
    }
}