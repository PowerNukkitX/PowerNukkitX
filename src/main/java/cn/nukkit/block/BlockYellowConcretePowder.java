package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(YELLOW_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockYellowConcrete();
    }
}