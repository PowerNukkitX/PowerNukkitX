package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockHangingSign;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockBirchHangingSign extends BlockHangingSign {
    public BlockBirchHangingSign() {}

    public int getId() {
        return BIRCH_HANGING_SIGN;
    }

    public String getName() {
        return "Birch Hanging Sign";
    }
}
