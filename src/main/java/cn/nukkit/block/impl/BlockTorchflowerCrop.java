package cn.nukkit.block.impl;

import cn.nukkit.block.BlockTransparentMeta;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.IntBlockProperty;

public class BlockTorchflowerCrop extends BlockTransparentMeta {
    public static final IntBlockProperty GROWTH = new IntBlockProperty("growth", false, 7);
    public static final BlockProperties PROPERTIES = new BlockProperties(GROWTH);

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTorchflowerCrop() {}

    public BlockTorchflowerCrop(int meta) {
        super(meta);
    }

    public int getId() {
        return TORCHFLOWER_CROP;
    }

    public String getName() {
        return "Torchflower Crop";
    }
}
