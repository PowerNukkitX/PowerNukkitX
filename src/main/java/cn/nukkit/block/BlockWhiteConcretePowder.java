package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(WHITE_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockWhiteConcrete();
    }
}