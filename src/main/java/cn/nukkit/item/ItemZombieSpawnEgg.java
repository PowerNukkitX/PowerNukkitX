package cn.nukkit.item;

import cn.nukkit.entity.EntityID;
import cn.nukkit.registry.Registries;

public class ItemZombieSpawnEgg extends ItemSpawnEgg {
    /**
     * @deprecated 
     */
    
    public ItemZombieSpawnEgg() {
        super(ZOMBIE_SPAWN_EGG);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEntityNetworkId() {
        return Registries.ENTITY.getEntityNetworkId(EntityID.ZOMBIE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
    }
}