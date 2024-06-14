package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOakDoubleSlab extends BlockDoubleWoodenSlab {
     public static final BlockProperties PROPERTIES = new BlockProperties(OAK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockOakDoubleSlab(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getSlabName() {
        return "Oak";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockOakSlab.PROPERTIES.getDefaultState();
    }
}