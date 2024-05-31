package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledPolishedBlackstone extends BlockBlackstone {
    public static final BlockProperties $1 = new BlockProperties(CHISELED_POLISHED_BLACKSTONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledPolishedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledPolishedBlackstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Chiseled Polished Blackstone";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }
}