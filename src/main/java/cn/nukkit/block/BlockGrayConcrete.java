package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(GRAY_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGrayConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGrayConcrete(BlockState blockstate) {
        super(blockstate);
    }
}