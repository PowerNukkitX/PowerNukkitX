package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedDeepslate extends BlockCobbledDeepslate {
    public static final BlockProperties $1 = new BlockProperties(POLISHED_DEEPSLATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedDeepslate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedDeepslate(BlockState blockstate) {
        super(blockstate);
    }
}