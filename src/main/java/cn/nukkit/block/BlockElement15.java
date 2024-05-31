package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement15 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_15");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement15() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement15(BlockState blockstate) {
        super(blockstate);
    }
}