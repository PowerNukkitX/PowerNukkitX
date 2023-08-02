package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

//todo complete
@PowerNukkitXOnly
@Since("1.20.10-r2")
public class BlockTorchflower extends BlockTransparent {
    public BlockTorchflower() {
    }

    public int getId() {
        return TORCHFLOWER;
    }

    public String getName() {
        return "Torchflower";
    }
}