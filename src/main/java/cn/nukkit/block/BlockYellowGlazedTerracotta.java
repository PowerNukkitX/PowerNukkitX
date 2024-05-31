package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockYellowGlazedTerracotta extends BlockGlazedTerracotta {
    public static final BlockProperties $1 = new BlockProperties(YELLOW_GLAZED_TERRACOTTA, CommonBlockProperties.FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowGlazedTerracotta() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowGlazedTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}