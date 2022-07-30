package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface EntityTamable {
    int DATA_TAMED_FLAG = 16;
    int DATA_OWNER_NAME = 17;
}
