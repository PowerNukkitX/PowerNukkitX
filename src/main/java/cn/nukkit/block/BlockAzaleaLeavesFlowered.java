package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PERSISTENT_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.UPDATE_BIT;

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
