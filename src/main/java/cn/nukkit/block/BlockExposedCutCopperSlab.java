package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCutCopperSlab extends BlockCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    public BlockExposedCutCopperSlab(BlockState blockstate,String doubleSlabId) {
        super(blockstate, doubleSlabId);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}