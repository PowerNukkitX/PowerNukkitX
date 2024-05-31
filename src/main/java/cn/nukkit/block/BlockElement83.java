package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement83 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_83");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement83() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement83(BlockState blockstate) {
        super(blockstate);
    }
}