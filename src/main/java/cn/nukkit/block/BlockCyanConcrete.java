package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCyanConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(CYAN_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCyanConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCyanConcrete(BlockState blockstate) {
        super(blockstate);
    }
}