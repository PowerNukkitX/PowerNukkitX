package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBrainCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brain_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrainCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrainCoral(BlockState blockstate) {
        super(blockstate);
    }
}