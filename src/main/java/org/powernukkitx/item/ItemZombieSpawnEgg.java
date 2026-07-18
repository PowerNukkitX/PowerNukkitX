package org.powernukkitx.item;

import org.powernukkitx.entity.EntityID;
import org.powernukkitx.registry.Registries;

public class ItemZombieSpawnEgg extends ItemSpawnEgg {
    public ItemZombieSpawnEgg() {
        super(ZOMBIE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return Registries.ENTITY.getEntityNetworkId(EntityID.ZOMBIE);
    }

    @Override
    public void setDamage(int meta) {
    }
}