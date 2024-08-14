package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedGraniteSlab extends BlockGraniteSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_GRANITE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockPolishedGraniteSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Polished Granite";
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}
