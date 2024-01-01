package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrackedDeepslateTiles extends BlockDeepslateTiles {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cracked_deepslate_tiles");

    @Override
    public @NotNull BlockProperties getProperties() {
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