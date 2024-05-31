package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties $1 = new BlockProperties(RED_GLAZED_TERRACOTTA, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRedGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRedGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}