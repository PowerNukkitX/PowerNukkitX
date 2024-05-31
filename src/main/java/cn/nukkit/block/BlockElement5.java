package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement5 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_5");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement5() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement5(BlockState blockstate) {
        super(blockstate);
    }
}