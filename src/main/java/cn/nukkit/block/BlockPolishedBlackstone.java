package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstone extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_blackstone");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstone(BlockState blockstate) {
        super(blockstate);
    }
}