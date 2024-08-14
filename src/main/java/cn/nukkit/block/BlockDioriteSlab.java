package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDioriteSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(DIORITE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockDioriteSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Diorite";
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
