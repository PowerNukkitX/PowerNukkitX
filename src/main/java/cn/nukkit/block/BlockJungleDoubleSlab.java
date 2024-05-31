package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleDoubleSlab extends BlockDoubleWoodenSlab {
     public static final BlockProperties $1 = new BlockProperties(JUNGLE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockJungleDoubleSlab(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Jungle";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSingleSlabId() {
        return JUNGLE_SLAB;
    }
}