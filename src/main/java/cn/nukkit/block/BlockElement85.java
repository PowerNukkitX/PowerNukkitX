package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement85 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_85");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement85() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement85(BlockState blockstate) {
        super(blockstate);
    }
}