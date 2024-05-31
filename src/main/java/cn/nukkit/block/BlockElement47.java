package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement47 extends Block {
    public static final BlockProperties $1 = new BlockProperties("minecraft:element_47");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockElement47() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockElement47(BlockState blockstate) {
        super(blockstate);
    }
}