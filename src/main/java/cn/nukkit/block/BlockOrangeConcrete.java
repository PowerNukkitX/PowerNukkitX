package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(ORANGE_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeConcrete(BlockState blockstate) {
        super(blockstate);
    }
}