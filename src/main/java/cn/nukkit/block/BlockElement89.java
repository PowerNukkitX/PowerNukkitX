package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement89 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_89");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement89() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement89(BlockState blockstate) {
        super(blockstate);
    }
}