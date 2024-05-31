package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement103 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_103");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement103() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement103(BlockState blockstate) {
        super(blockstate);
    }
}