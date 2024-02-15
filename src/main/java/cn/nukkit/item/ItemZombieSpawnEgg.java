package cn.nukkit.item;

import cn.nukkit.entity.EntityID;
import cn.nukkit.registry.Registries;

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