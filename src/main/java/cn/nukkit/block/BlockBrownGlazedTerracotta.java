package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties $1 = new BlockProperties(BROWN_GLAZED_TERRACOTTA, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}