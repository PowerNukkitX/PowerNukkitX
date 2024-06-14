package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakDoubleSlab extends BlockDoubleWoodenSlab {
     public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockDarkOakDoubleSlab(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getSlabName() {
        return "Dark Oak";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockDarkOakSlab.PROPERTIES.getDefaultState();
    }
}