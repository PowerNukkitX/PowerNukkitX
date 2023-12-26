package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockColoredTorchBp extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:colored_torch_bp", CommonBlockProperties.COLOR_BIT, CommonBlockProperties.TORCH_FACING_DIRECTION);

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