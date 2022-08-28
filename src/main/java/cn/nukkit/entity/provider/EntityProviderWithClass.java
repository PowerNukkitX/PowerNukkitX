package cn.nukkit.entity.provider;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public interface EntityProviderWithClass {
    Class<? extends Entity> getEntityClass();
}
