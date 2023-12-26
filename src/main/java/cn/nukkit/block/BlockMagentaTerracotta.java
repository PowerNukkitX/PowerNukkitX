package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaTerracotta extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_terracotta");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaTerracotta() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaTerracotta(BlockState blockstate) {
        super(blockstate);
    }
}