package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement93 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_93");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement93() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement93(BlockState blockstate) {
        super(blockstate);
    }
}