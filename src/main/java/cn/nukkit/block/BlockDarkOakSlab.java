package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakSlab extends BlockWoodenSlab {
     public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockDarkOakSlab(BlockState blockstate) {
         super(blockstate,DARK_OAK_DOUBLE_SLAB);
     }

    @Override
    public String getSlabName() {
        return "Dark Oak";
    }
}