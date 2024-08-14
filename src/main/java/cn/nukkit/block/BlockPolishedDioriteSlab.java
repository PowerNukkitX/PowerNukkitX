package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDioriteSlab extends BlockDioriteSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_DIORITE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockPolishedDioriteSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Polished Diorite";
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}
