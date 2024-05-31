package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateCoalOre extends BlockCoalOre {
    public static final BlockProperties $1 = new BlockProperties(DEEPSLATE_COAL_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateCoalOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateCoalOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Deeplsate Coal Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 4.5;
    }
}