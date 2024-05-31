package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(PURPLE_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPurpleConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPurpleConcrete(BlockState blockstate) {
        super(blockstate);
    }
}