package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrainCoral extends BlockCoral {
    public static final BlockProperties $1 = new BlockProperties(BRAIN_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBrainCoral() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBrainCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isDead() {
        return false;
    }

    @Override
    public Block getDeadCoral() {
        return new BlockDeadBrainCoral();
    }
}