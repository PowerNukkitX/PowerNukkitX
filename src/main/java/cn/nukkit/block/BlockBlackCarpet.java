package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBlackCarpet extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_carpet");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackCarpet() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackCarpet(BlockState blockstate) {
        super(blockstate);
    }
}