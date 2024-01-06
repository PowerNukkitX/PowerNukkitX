package cn.nukkit.item;

import cn.nukkit.item.Item;

public class ItemStraySpawnEgg extends ItemSpawnEgg {
    public ItemStraySpawnEgg() {
        super(STRAY_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 46;
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}