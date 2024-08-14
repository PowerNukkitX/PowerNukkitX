package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockAndesiteSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(ANDESITE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockAndesiteSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Andesite";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getResistance() {
        return 7.5;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
}
