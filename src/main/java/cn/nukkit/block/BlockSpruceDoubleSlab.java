package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceDoubleSlab extends BlockDoubleWoodenSlab {
    public static final BlockProperties $1 = new BlockProperties(SPRUCE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSpruceDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Spruce";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSingleSlabId() {
        return SPRUCE_SLAB;
    }
}