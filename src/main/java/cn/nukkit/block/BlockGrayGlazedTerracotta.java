package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGrayGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRAY_GLAZED_TERRACOTTA, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}