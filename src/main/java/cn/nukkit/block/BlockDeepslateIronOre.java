package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateIronOre extends BlockIronOre {
    public static final BlockProperties $1 = new BlockProperties(DEEPSLATE_IRON_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateIronOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateIronOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Deepslate Iron Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 4.5;
    }
}