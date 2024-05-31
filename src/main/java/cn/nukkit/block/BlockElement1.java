package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement1 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_1");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement1() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement1(BlockState blockstate) {
        super(blockstate);
    }
}