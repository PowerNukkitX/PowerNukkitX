package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement94 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_94");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement94() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement94(BlockState blockstate) {
        super(blockstate);
    }
}