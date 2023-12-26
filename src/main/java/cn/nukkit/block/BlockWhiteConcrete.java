package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteConcrete(BlockState blockstate) {
        super(blockstate);
    }
}