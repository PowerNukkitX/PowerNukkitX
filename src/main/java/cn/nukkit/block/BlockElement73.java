package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement73 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_73");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement73() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement73(BlockState blockstate) {
        super(blockstate);
    }
}