package cn.nukkit.block.copper.chain;

import cn.nukkit.block.BlockConnectable;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWaxedCopperChain extends BlockCopperChain {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_CHAIN, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperChain() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperChain(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Copper Chain";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
