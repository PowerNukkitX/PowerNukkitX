package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateDiamondOre extends BlockDiamondOre {
    public static final BlockProperties $1 = new BlockProperties(DEEPSLATE_DIAMOND_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateDiamondOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateDiamondOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Deepslate Diamond Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 4.5;
    }
}