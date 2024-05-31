package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(BLACK_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBlackConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlackConcrete(BlockState blockstate) {
        super(blockstate);
    }
}