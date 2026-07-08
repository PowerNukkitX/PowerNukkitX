package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.PERSISTENT_BIT;
import static org.powernukkitx.block.property.CommonBlockProperties.UPDATE_BIT;

public class BlockAzaleaLeavesFlowered extends BlockAzaleaLeaves {
    public static final BlockProperties PROPERTIES = new BlockProperties(AZALEA_LEAVES_FLOWERED,PERSISTENT_BIT, UPDATE_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAzaleaLeavesFlowered() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAzaleaLeavesFlowered(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Azalea Leaves Flowered";
    }

}
