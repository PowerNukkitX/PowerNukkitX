package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.UPPER_BLOCK_BIT;

//todo complete
public class BlockPitcherCrop extends BlockCrops {
    public static final BlockProperties $1 = new BlockProperties(PITCHER_CROP, CommonBlockProperties.GROWTH, UPPER_BLOCK_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPitcherCrop() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPitcherCrop(BlockState blockstate) {
        super(blockstate);
    }
    /**
     * @deprecated 
     */
    

    public String getName() {
        return "Pitcher Crop";
    }
}