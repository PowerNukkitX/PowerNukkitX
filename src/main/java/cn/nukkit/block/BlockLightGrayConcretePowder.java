package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightGrayConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_GRAY_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightGrayConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightGrayConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockLightGrayConcrete();
    }
}