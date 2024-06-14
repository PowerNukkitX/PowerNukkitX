package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAcaciaDoubleSlab extends BlockDoubleWoodenSlab {
     public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

     @Override
     @NotNull
     public BlockProperties getProperties() {
        return PROPERTIES;
     }

     public BlockAcaciaDoubleSlab(BlockState blockstate) {
         super(blockstate);
     }

    @Override
    public String getSlabName() {
        return "Acacia";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockAcaciaSlab.PROPERTIES.getDefaultState();
    }
}