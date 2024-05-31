package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrackedDeepslateTiles extends BlockDeepslateTiles {
    public static final BlockProperties $1 = new BlockProperties(CRACKED_DEEPSLATE_TILES);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCrackedDeepslateTiles() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCrackedDeepslateTiles(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cracked Deepslate Tiles";
    }
}