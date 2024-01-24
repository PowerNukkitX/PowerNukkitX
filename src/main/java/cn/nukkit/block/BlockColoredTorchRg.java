package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockColoredTorchRg extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(COLORED_TORCH_RG, CommonBlockProperties.COLOR_BIT, CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockColoredTorchRg() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockColoredTorchRg(BlockState blockstate) {
        super(blockstate);
    }
}