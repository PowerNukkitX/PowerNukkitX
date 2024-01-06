package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchStairs extends BlockStairsWood {
    public static final BlockProperties PROPERTIES = new BlockProperties(BIRCH_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBirchStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBirchStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Birch Wood Stairs";
    }
}