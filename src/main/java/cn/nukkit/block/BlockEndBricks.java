package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockEndBricks extends BlockEndStone {
    public static final BlockProperties $1 = new BlockProperties(END_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockEndBricks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockEndBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "End Stone Bricks";
    }
}