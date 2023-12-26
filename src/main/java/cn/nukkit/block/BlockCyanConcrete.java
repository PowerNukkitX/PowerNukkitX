package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCyanConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanConcrete(BlockState blockstate) {
        super(blockstate);
    }
}