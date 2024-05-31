package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaConcretePowder extends BlockConcretePowder {
    public static final BlockProperties $1 = new BlockProperties(MAGENTA_CONCRETE_POWDER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMagentaConcretePowder() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMagentaConcretePowder(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockConcrete getConcrete() {
        return new BlockMagentaConcrete();
    }
}