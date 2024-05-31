package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement19 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_19");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement19() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement19(BlockState blockstate) {
        super(blockstate);
    }
}