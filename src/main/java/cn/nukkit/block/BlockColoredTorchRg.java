package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockColoredTorchRg extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:colored_torch_rg", CommonBlockProperties.COLOR_BIT, CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockColoredTorchRg() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockColoredTorchRg(BlockState blockstate) {
        super(blockstate);
    }
}