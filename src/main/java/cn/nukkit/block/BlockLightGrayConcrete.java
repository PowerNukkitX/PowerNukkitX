package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayConcrete extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_concrete");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayConcrete() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayConcrete(BlockState blockstate) {
        super(blockstate);
    }
}