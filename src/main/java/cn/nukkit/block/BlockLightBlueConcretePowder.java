package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_BLUE_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockLightBlueConcrete();
    }
}