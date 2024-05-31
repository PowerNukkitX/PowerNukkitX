package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement43 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_43");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement43() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement43(BlockState blockstate) {
        super(blockstate);
    }
}