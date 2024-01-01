package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCutCopper extends BlockCutCopper {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:exposed_cut_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCutCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCutCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Exposed Cut Copper";
    }

    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}