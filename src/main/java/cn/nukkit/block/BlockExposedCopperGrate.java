package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCopperGrate extends BlockCopperGrateBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}