package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateEmeraldOre extends BlockEmeraldOre  {
    public static final BlockProperties $1 = new BlockProperties(DEEPSLATE_EMERALD_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateEmeraldOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateEmeraldOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Deepslate Emerald Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 4.5;
    }
}