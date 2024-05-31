package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement48 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_48");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement48() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement48(BlockState blockstate) {
        super(blockstate);
    }
}