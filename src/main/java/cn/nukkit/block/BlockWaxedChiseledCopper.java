package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedChiseledCopper extends BlockChiseledCopperBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}