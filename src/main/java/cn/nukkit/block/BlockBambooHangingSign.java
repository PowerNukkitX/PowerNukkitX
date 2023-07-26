package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBambooHangingSign extends BlockHangingSign {
    public BlockBambooHangingSign() {}

    public int getId() {
        return BAMBOO_HANGING_SIGN;
    }

    public String getName() {
        return "Bamboo Hanging Sign";
    }
}
