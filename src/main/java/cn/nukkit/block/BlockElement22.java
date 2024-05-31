package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement22 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_22");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement22() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement22(BlockState blockstate) {
        super(blockstate);
    }
}