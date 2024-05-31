package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement36 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_36");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement36() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement36(BlockState blockstate) {
        super(blockstate);
    }
}