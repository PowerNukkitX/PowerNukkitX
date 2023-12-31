package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCopper extends BlockCopperBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Oxidized Copper";
    }

    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}