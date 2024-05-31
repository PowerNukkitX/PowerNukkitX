package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedConcrete extends BlockConcrete {
    public static final BlockProperties $1 = new BlockProperties(RED_CONCRETE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRedConcrete() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRedConcrete(BlockState blockstate) {
        super(blockstate);
    }
}