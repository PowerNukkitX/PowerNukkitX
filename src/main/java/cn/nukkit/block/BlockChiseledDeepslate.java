package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockChiseledDeepslate extends BlockCobbledDeepslate {
    public static final BlockProperties $1 = new BlockProperties(CHISELED_DEEPSLATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledDeepslate() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockChiseledDeepslate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Chiseled Deepslate";
    }
}