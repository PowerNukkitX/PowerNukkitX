package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockColoredTorchRg extends Block {
    public static final BlockProperties $1 = new BlockProperties(COLORED_TORCH_RG, CommonBlockProperties.COLOR_BIT, CommonBlockProperties.TORCH_FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockColoredTorchRg() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockColoredTorchRg(BlockState blockstate) {
        super(blockstate);
    }
}