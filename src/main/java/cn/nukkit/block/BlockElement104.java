package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement104 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_104");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement104() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement104(BlockState blockstate) {
        super(blockstate);
    }
}