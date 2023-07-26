package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockOakHangingSign extends BlockHangingSign {
    public BlockOakHangingSign() {}

    public int getId() {
        return OAK_HANGING_SIGN;
    }

    public String getName() {
        return "Oak Hanging Sign";
    }
}
