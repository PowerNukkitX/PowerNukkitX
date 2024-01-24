package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

public class BlockSoulTorch extends BlockTorch {
    public static final BlockProperties PROPERTIES = new BlockProperties(SOUL_TORCH, TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulTorch(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Soul Torch";
    }

    @Override
    public int getLightLevel() {
        return 10;
    }
}
