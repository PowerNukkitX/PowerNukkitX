package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedChiseledCopper extends BlockChiseledCopperBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}