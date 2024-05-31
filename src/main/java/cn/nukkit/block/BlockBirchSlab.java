package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBirchSlab extends BlockWoodenSlab {
     public static final BlockProperties $1 = new BlockProperties(BIRCH_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockBirchSlab(BlockState blockstate) {
         super(blockstate,BIRCH_DOUBLE_SLAB);
     }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Birch";
    }
}