package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedDoubleCutCopperSlab extends BlockOxidizedDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockWaxedOxidizedCutCopperSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}