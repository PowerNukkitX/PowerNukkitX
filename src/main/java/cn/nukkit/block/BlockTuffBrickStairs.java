package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBrickStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_BRICK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffBrickStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBrickStairs(BlockState blockstate) {
        super(blockstate);
    }
}