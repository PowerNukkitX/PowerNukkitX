package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCopper extends BlockCopperBlock {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weathered_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Weathered Copper";
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}