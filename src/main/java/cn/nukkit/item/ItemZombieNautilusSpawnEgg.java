package cn.nukkit.item;

import cn.nukkit.entity.EntityID;
import cn.nukkit.registry.Registries;

public class ItemZombieNautilusSpawnEgg extends ItemSpawnEgg {
    public ItemZombieNautilusSpawnEgg() {
        super(ZOMBIE_NAUTILUS_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return Registries.ENTITY.getEntityNetworkId(EntityID.ZOMBIE_NAUTILUS);
    }

    @Override
    public void setDamage(int meta) {
    }
}