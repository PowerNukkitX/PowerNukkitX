package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement30 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_30");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement30() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement30(BlockState blockstate) {
        super(blockstate);
    }
}