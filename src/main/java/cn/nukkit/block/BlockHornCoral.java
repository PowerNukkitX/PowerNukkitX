package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHornCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:horn_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHornCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHornCoral(BlockState blockstate) {
        super(blockstate);
    }
}