package org.powernukkitx.item;

import org.powernukkitx.entity.EntityID;
import org.powernukkitx.registry.Registries;

public class ItemNautilusSpawnEgg extends ItemSpawnEgg {
    public ItemNautilusSpawnEgg() {
        super(NAUTILUS_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return Registries.ENTITY.getEntityNetworkId(EntityID.NAUTILUS);
    }

    @Override
    public void setDamage(int meta) {
    }
}