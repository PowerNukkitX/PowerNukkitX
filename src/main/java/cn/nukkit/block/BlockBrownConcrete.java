package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(BROWN_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBrownConcrete(BlockState blockstate) {
        super(blockstate);
    }
}