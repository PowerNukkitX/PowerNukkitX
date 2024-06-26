package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockCopperGrate extends BlockCopperGrateBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_GRATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperGrate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperGrate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}