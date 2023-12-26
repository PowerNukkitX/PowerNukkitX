package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockLitRedstoneLamp extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lit_redstone_lamp");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLitRedstoneLamp() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLitRedstoneLamp(BlockState blockstate) {
        super(blockstate);
    }
}