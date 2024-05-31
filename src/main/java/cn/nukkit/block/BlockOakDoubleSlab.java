package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOakDoubleSlab extends BlockDoubleWoodenSlab {
     public static final BlockProperties $1 = new BlockProperties(OAK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockOakDoubleSlab(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Oak";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSingleSlabId() {
        return OAK_SLAB;
    }
}