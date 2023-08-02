package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.10-r2")
public class BlockCherryHangingSign extends BlockHangingSign {
    public BlockCherryHangingSign() {
    }

    public int getId() {
        return CHERRY_HANGING_SIGN;
    }

    public String getName() {
        return "Cherry Hanging Sign";
    }
}
