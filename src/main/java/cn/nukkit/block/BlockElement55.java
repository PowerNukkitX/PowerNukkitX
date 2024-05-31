package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement55 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_55");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement55() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement55(BlockState blockstate) {
        super(blockstate);
    }
}