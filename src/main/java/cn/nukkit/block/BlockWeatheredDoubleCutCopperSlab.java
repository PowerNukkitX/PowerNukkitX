package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredDoubleCutCopperSlab extends BlockDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEATHERED_DOUBLE_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockWeatheredCutCopperSlab.PROPERTIES.getDefaultState();
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}