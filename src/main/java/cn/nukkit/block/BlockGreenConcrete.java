package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGreenConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(GREEN_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGreenConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGreenConcrete(BlockState blockstate) {
        super(blockstate);
    }
}