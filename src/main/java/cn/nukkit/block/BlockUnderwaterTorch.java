package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnderwaterTorch extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:underwater_torch", CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnderwaterTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnderwaterTorch(BlockState blockstate) {
        super(blockstate);
    }
}