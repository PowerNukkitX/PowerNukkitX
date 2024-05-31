package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement51 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_51");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement51() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement51(BlockState blockstate) {
        super(blockstate);
    }
}