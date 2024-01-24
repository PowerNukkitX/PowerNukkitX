package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockTuffStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTuffStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffStairs(BlockState blockstate) {
        super(blockstate);
    }
}