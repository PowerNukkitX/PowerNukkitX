package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopperGrate extends BlockCopperGrateBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperGrate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}