package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateCopperOre extends BlockCopperOre {
    public static final BlockProperties $1 = new BlockProperties(DEEPSLATE_COPPER_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateCopperOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateCopperOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Deepslate Copper Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 4.5;
    }
}