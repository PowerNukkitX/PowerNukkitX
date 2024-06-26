package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredChiseledCopper extends BlockChiseledCopperBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEATHERED_CHISELED_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredChiseledCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredChiseledCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}