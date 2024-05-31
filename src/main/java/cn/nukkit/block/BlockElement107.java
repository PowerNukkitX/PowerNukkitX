package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement107 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_107");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement107() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement107(BlockState blockstate) {
        super(blockstate);
    }
}