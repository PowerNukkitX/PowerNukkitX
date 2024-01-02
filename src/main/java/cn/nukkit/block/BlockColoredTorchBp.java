package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockColoredTorchBp extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(COLORED_TORCH_BP, CommonBlockProperties.COLOR_BIT, CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockColoredTorchBp() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockColoredTorchBp(BlockState blockstate) {
        super(blockstate);
    }
}