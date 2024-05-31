package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement37 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_37");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement37() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement37(BlockState blockstate) {
        super(blockstate);
    }
}