package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement11 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_11");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement11() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement11(BlockState blockstate) {
        super(blockstate);
    }
}