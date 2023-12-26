package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockUnlitRedstoneTorch extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:unlit_redstone_torch", CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockUnlitRedstoneTorch() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockUnlitRedstoneTorch(BlockState blockstate) {
        super(blockstate);
    }
}