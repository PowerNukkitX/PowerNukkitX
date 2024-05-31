package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPinkGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties $1 = new BlockProperties(PINK_GLAZED_TERRACOTTA, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}