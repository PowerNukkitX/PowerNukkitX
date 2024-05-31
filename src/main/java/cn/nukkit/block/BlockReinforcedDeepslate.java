package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

public class BlockReinforcedDeepslate extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(REINFORCED_DEEPSLATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockReinforcedDeepslate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockReinforcedDeepslate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "ReinForced DeepSlate";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 1200.0;
    }
}
