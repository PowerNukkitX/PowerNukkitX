package cn.nukkit.entity.provider;

import cn.nukkit.entity.Entity;


public interface EntityProviderWithClass {
    Class<? extends Entity> getEntityClass();
}
