package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrackedDeepslateTiles extends BlockDeepslateTiles {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRACKED_DEEPSLATE_TILES);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedDeepslateTiles() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedDeepslateTiles(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cracked Deepslate Tiles";
    }
}