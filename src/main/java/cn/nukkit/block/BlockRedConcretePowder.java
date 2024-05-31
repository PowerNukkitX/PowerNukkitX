package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(RED_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRedConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRedConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockRedConcrete();
    }
}