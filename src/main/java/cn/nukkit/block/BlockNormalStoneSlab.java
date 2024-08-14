package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockNormalStoneSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(NORMAL_STONE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public BlockNormalStoneSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getSlabName() {
        return "Stone";
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
        return 10;
    }
}
