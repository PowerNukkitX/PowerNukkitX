package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSilverGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties PROPERTIES = new BlockProperties(SILVER_GLAZED_TERRACOTTA, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSilverGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSilverGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}