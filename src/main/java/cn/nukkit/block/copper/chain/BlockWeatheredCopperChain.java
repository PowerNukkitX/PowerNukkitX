package cn.nukkit.block.copper.chain;

import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWeatheredCopperChain extends BlockCopperChain {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEATHERED_COPPER_CHAIN, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCopperChain() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCopperChain(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Weathered Copper Chain";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}
