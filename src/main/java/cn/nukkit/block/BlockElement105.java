package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement105 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_105");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement105() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement105(BlockState blockstate) {
        super(blockstate);
    }
}