package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(PINK_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkConcrete(BlockState blockstate) {
        super(blockstate);
    }
}