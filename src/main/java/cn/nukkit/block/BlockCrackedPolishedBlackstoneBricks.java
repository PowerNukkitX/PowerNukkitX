package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrackedPolishedBlackstoneBricks extends BlockBlackstone {
    public static final BlockProperties $1 = new BlockProperties(CRACKED_POLISHED_BLACKSTONE_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCrackedPolishedBlackstoneBricks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCrackedPolishedBlackstoneBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cracked Polished Blackstone Bricks";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }
}