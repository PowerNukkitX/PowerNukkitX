package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMossyCobblestoneSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(MOSSY_COBBLESTONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockMossyCobblestoneSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Mossy Cobblestone";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return false;
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}
