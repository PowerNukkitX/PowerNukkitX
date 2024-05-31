package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties $1 = new BlockProperties(ORANGE_GLAZED_TERRACOTTA, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}