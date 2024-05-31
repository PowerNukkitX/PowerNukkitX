package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHornCoral extends BlockCoral {
    public static final BlockProperties $1 = new BlockProperties(HORN_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockHornCoral() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockHornCoral(BlockState blockstate) {
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
        return new BlockDeadHornCoral();
    }
}