package org.powernukkitx.block.copper.golem;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockWaxedCopperGolemStatue extends BlockCopperGolemStatue {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_GOLEM_STATUE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperGolemStatue() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperGolemStatue(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Copper Golem Statue";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}
