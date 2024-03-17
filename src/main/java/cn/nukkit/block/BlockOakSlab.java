package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOakSlab extends BlockWoodenSlab {
     public static final BlockProperties PROPERTIES = new BlockProperties(OAK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockOakSlab(BlockState blockstate) {
         super(blockstate,OAK_DOUBLE_SLAB);
     }

    @Override
    public String getSlabName() {
        return "Oak";
    }
}