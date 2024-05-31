package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement81 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_81");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement81() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement81(BlockState blockstate) {
        super(blockstate);
    }
}