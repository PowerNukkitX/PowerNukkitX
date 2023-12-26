package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGrayConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayConcrete(BlockState blockstate) {
        super(blockstate);
    }
}