package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBlackWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackWool(BlockState blockstate) {
        super(blockstate);
    }
}