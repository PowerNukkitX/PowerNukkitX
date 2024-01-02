package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_GLAZED_TERRACOTTA, CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}