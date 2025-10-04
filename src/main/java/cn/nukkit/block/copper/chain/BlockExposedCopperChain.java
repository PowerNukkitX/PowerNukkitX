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
public class BlockExposedCopperChain extends BlockCopperChain {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_COPPER_CHAIN, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperChain() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperChain(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Exposed Copper Chain";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}
