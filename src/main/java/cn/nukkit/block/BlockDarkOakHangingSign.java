package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.0-r2")
public class BlockDarkOakHangingSign extends BlockHangingSign {
    public BlockDarkOakHangingSign() {}

    public int getId() {
        return DARK_OAK_HANGING_SIGN;
    }

    public String getName() {
        return "Dark Oak Hanging Sign";
    }
}
