package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeepslateLapisOre extends BlockLapisOre {
    public static final BlockProperties $1 = new BlockProperties(DEEPSLATE_LAPIS_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateLapisOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDeepslateLapisOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Deepslate Lapis Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 4.5;
    }
}