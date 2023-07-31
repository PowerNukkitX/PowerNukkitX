package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockHangingSign;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockCrimsonHangingSign extends BlockHangingSign {
    public BlockCrimsonHangingSign() {}

    public int getId() {
        return CRIMSON_HANGING_SIGN;
    }

    public String getName() {
        return "Crimson Hanging Sign";
    }
}
