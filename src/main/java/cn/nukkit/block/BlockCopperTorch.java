package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

public class BlockCopperTorch extends BlockTorch {
    public static final BlockProperties PROPERTIES = new BlockProperties(COPPER_TORCH, TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperTorch(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Copper Torch";
    }

    @Override
    public int getLightLevel() {
        return 14;
    }
}
