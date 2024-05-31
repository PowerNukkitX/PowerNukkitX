package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement33 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_33");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement33() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement33(BlockState blockstate) {
        super(blockstate);
    }
}