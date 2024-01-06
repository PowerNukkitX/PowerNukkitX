package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBambooStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooStairs(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Bamboo Stairs";
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
}