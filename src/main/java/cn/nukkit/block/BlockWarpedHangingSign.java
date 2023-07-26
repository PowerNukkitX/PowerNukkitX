package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockWarpedHangingSign extends BlockHangingSign {
    public BlockWarpedHangingSign() {}

    public int getId() {
        return WARPED_HANGING_SIGN;
    }

    public String getName() {
        return "Warped Hanging Sign";
    }
}
