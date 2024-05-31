package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(LIME_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLimeConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLimeConcrete(BlockState blockstate) {
        super(blockstate);
    }
}