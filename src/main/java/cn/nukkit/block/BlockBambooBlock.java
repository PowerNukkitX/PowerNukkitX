package cn.nukkit.block;

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

    @Override
    @NotNull public  BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Bamboo Block";
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
    public BlockState getStrippedState() {
        return BlockStrippedBambooBlock.PROPERTIES.getDefaultState();
    }
}