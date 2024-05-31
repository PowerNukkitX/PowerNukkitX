package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement71 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_71");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement71() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement71(BlockState blockstate) {
        super(blockstate);
    }
}