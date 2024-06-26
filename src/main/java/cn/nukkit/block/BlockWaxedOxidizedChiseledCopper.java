package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedChiseledCopper extends BlockChiseledCopperBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_OXIDIZED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}