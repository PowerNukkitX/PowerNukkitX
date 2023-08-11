package cn.nukkit.block.impl;

import cn.nukkit.block.BlockCrops;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.BooleanBlockProperty;
import org.jetbrains.annotations.NotNull;

public class BlockPitcherCrop extends BlockCrops {

    public static final BooleanBlockProperty UPPER_BLOCK = new BooleanBlockProperty("upper_block_bit", false);


    public static final BlockProperties PROPERTIES = new BlockProperties(GROWTH, UPPER_BLOCK);

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPitcherCrop() {
        this(0);
    }

    public BlockPitcherCrop(int meta) {
        super(meta);
    }

    public int getId() {
        return PITCHER_CROP;
    }

    public String getName() {
        return "Pitcher Crop";
    }
}
