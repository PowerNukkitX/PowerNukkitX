package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakDoubleSlab extends BlockDoubleWoodenSlab {
     public static final BlockProperties $1 = new BlockProperties(DARK_OAK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockDarkOakDoubleSlab(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Dark Oak";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSingleSlabId() {
        return DARK_OAK_SLAB;
    }
}