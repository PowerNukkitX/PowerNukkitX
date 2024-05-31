package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadBrainCoral extends BlockBrainCoral {
    public static final BlockProperties $1 = new BlockProperties(DEAD_BRAIN_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadBrainCoral() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadBrainCoral(BlockState blockstate) {
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