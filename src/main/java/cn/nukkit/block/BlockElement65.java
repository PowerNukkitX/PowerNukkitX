package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement65 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_65");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement65() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement65(BlockState blockstate) {
        super(blockstate);
    }
}