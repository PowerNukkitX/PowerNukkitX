package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleSlab extends BlockWoodenSlab {
     public static final BlockProperties $1 = new BlockProperties(JUNGLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockJungleSlab(BlockState blockstate) {
         super(blockstate,JUNGLE_DOUBLE_SLAB);
     }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Jungle";
    }
}