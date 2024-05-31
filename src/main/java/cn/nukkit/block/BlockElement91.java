package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement91 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_91");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement91() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement91(BlockState blockstate) {
        super(blockstate);
    }
}