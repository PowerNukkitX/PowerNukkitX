package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDeadTubeCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dead_tube_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadTubeCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadTubeCoral(BlockState blockstate) {
        super(blockstate);
    }
}