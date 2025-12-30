package cn.nukkit.education.block;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnderwaterTorch extends BlockTorch {
    public static final BlockProperties PROPERTIES = new BlockProperties(UNDERWATER_TORCH,  CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnderwaterTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnderwaterTorch(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }
}