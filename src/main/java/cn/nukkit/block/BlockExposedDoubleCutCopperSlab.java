package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedDoubleCutCopperSlab extends BlockDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_DOUBLE_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockExposedCutCopperSlab.PROPERTIES.getDefaultState();
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}