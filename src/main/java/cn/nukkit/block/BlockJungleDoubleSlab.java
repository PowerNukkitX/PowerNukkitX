package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockJungleDoubleSlab extends BlockDoubleWoodenSlab {
     public static final BlockProperties PROPERTIES = new BlockProperties(JUNGLE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockJungleDoubleSlab(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getSlabName() {
        return "Jungle";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockJungleSlab.PROPERTIES.getDefaultState();
    }
}