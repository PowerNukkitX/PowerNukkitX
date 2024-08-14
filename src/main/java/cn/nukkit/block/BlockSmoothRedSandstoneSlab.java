package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSmoothRedSandstoneSlab extends BlockRedSandstoneSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(SMOOTH_RED_SANDSTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockSmoothRedSandstoneSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Smooth Red Sandstone";
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}
