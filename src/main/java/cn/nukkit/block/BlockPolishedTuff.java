package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPolishedTuff extends BlockTuff {
    public static final BlockProperties $1 = new BlockProperties(POLISHED_TUFF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedTuff() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPolishedTuff(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Polished Tuff";
    }
}