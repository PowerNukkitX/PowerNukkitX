package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement74 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_74");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement74() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement74(BlockState blockstate) {
        super(blockstate);
    }
}