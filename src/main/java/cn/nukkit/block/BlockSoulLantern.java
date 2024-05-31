package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.HANGING;

public class BlockSoulLantern extends BlockLantern {
    public static final BlockProperties $1 = new BlockProperties(SOUL_LANTERN, HANGING);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulLantern() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulLantern(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Soul Lantern";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 10;
    }

}
