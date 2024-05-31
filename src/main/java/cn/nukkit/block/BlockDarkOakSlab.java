package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakSlab extends BlockWoodenSlab {
     public static final BlockProperties $1 = new BlockProperties(DARK_OAK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }
    /**
     * @deprecated 
     */
    

     public BlockDarkOakSlab(BlockState blockstate) {
         super(blockstate,DARK_OAK_DOUBLE_SLAB);
     }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        return "Dark Oak";
    }
}