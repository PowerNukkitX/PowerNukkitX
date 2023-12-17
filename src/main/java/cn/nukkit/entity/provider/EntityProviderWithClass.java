package cn.nukkit.entity.provider;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;


public interface EntityProviderWithClass {
    Class<? extends Entity> getEntityClass();
}
