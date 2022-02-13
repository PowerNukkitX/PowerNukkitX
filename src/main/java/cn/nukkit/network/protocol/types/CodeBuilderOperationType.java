package cn.nukkit.network.protocol.types;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public enum CodeBuilderOperationType {
    NONE,
    GET,
    SET,
    RESET
}
