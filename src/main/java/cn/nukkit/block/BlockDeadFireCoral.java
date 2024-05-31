package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadFireCoral extends BlockFireCoral {
    public static final BlockProperties $1 = new BlockProperties(DEAD_FIRE_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadFireCoral() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeadFireCoral(BlockState blockstate) {
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