package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(BLUE_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueConcrete(BlockState blockstate) {
        super(blockstate);
    }
}