package cn.nukkit.block.copper.lightningrod;

import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWaxedLightningRod extends BlockLightningRod {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_LIGHTNING_ROD, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedLightningRod() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedLightningRod(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Lightning Rod";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
