package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(PINK_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockPinkConcrete();
    }
}