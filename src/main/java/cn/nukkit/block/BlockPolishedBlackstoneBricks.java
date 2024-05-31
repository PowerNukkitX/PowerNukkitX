package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneBricks extends BlockPolishedBlackstone {
    public static final BlockProperties $1 = new BlockProperties(POLISHED_BLACKSTONE_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedBlackstoneBricks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedBlackstoneBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Polished Blackstone Bricks";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.5;
    }
}