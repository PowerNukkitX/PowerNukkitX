package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopperGrate extends BlockCopperGrateBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCopperGrate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}