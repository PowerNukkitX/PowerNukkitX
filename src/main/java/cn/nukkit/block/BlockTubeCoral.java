package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockTubeCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:tube_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTubeCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTubeCoral(BlockState blockstate) {
        super(blockstate);
    }
}