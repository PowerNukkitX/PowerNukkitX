package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockHangingSign;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockAcaciaHangingSign extends BlockHangingSign {
    public BlockAcaciaHangingSign() {}

    public int getId() {
        return ACACIA_HANGING_SIGN;
    }

    public String getName() {
        return "Acacia Hanging Sign";
    }
}
