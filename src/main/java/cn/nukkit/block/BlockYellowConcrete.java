package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(YELLOW_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowConcrete(BlockState blockstate) {
        super(blockstate);
    }
}