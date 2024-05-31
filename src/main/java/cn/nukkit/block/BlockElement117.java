package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement117 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_117");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement117() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement117(BlockState blockstate) {
        super(blockstate);
    }
}