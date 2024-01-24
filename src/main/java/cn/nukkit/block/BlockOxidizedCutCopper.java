package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCutCopper extends BlockCutCopper {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXIDIZED_CUT_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCutCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cut Oxidized Copper";
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}