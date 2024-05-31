package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_GRAY_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightGrayConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightGrayConcrete(BlockState blockstate) {
        super(blockstate);
    }
}