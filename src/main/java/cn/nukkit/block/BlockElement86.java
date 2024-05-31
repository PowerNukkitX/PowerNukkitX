package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement86 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_86");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement86() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement86(BlockState blockstate) {
        super(blockstate);
    }
}