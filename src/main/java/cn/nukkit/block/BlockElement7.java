package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement7 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_7");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement7() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement7(BlockState blockstate) {
        super(blockstate);
    }
}