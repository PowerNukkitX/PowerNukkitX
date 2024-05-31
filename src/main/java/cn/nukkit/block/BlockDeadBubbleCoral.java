package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadBubbleCoral extends BlockBubbleCoral {
    public static final BlockProperties $1 = new BlockProperties(DEAD_BUBBLE_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadBubbleCoral() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadBubbleCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isDead() {
        return true;
    }
}