package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;

//todo complete
@PowerNukkitXOnly
@Since("1.20.10-r2")
public class BlockTorchflowerCrop extends BlockTransparentMeta {
    public static final IntBlockProperty GROWTH = new IntBlockProperty("growth", false, 7);
    public static final BlockProperties PROPERTIES = new BlockProperties(GROWTH);

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTorchflowerCrop() {
    }

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