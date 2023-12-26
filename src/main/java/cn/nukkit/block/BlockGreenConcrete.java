package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGreenConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenConcrete(BlockState blockstate) {
        super(blockstate);
    }
}