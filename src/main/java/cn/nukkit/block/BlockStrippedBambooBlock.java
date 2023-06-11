package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockStrippedBambooBlock extends BlockHangingSign {
    public BlockStrippedBambooBlock() {
    }

    public int getId() {
        return STRIPPED_BAMBOO_BLOCK;
    }

    public String getName() {
        return "Stripped Bamboo Block";
    }
}