package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement63 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_63");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement63() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement63(BlockState blockstate) {
        super(blockstate);
    }
}