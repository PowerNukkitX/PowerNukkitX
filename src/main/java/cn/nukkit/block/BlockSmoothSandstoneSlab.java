package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothSandstoneSlab extends BlockSandstoneSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_SANDSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockSmoothSandstoneSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Smooth Sandstone";
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}
