package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedDeepslate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_deepslate");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedDeepslate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedDeepslate(BlockState blockstate) {
        super(blockstate);
    }
}