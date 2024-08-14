package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGraniteSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(GRANITE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockGraniteSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Granite";
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
