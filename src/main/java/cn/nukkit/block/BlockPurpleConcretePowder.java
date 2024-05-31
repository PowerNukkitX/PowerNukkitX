package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(PURPLE_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPurpleConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPurpleConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockPurpleConcrete();
    }
}