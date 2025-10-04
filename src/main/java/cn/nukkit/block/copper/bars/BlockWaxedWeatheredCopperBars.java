package cn.nukkit.block.copper.bars;

import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWaxedWeatheredCopperBars extends BlockWaxedCopperBars {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_COPPER_BARS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCopperBars() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCopperBars(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Bars";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}
