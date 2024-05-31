package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCyanConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(CYAN_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCyanConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCyanConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockCyanConcrete();
    }

}