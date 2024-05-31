package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement2 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_2");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement2() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement2(BlockState blockstate) {
        super(blockstate);
    }
}