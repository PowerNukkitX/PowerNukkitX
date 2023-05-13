package cn.nukkit.entity.custom;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface CustomEntity {
    CustomEntityDefinition getDefinition();
}
