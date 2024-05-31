package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockFloweringAzalea extends BlockAzalea {

    public static final BlockProperties $1 = new BlockProperties(FLOWERING_AZALEA);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockFloweringAzalea() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockFloweringAzalea(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "FloweringAzalea";
    }

}
