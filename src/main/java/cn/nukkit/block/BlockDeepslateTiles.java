package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateTiles extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_tiles");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateTiles() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateTiles(BlockState blockstate) {
        super(blockstate);
    }
}