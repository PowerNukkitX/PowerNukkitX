package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement42 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_42");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement42() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement42(BlockState blockstate) {
        super(blockstate);
    }
}