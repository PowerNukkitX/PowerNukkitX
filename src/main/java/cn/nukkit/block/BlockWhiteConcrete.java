package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(WHITE_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteConcrete(BlockState blockstate) {
        super(blockstate);
    }
}