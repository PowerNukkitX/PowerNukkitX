package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.UPPER_BLOCK_BIT;

//todo complete
public class BlockPitcherCrop extends BlockCrops {
    public static final BlockProperties PROPERTIES = new BlockProperties(PITCHER_CROP,UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
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