package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedChiseledCopper extends BlockChiseledCopperBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}