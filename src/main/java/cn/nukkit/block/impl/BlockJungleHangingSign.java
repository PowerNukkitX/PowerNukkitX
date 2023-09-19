package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockHangingSign;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockJungleHangingSign extends BlockHangingSign {
    public BlockJungleHangingSign() {}

    public int getId() {
        return JUNGLE_HANGING_SIGN;
    }

    public String getName() {
        return "Jungle Hanging Sign";
    }
}
