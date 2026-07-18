package org.powernukkitx.block.copper.golem;

import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockExposedCopperGolemStatue extends BlockCopperGolemStatue {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_COPPER_GOLEM_STATUE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperGolemStatue() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperGolemStatue(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Exposed Copper Golem Statue";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}
