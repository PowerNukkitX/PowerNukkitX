package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.TORCH_FACING_DIRECTION;

public class BlockSoulTorch extends BlockTorch {
    public static final BlockProperties $1 = new BlockProperties(SOUL_TORCH, TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulTorch() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulTorch(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Soul Torch";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return 10;
    }
}
