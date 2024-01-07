package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.UPPER_BLOCK_BIT;

//todo complete
public class BlockPitcherCrop extends BlockCrops {
    public static final BlockProperties PROPERTIES = new BlockProperties(PITCHER_CROP, CommonBlockProperties.GROWTH, UPPER_BLOCK_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPitcherCrop() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPitcherCrop(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Pitcher Crop";
    }
}