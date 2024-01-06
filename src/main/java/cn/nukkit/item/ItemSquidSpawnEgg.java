package cn.nukkit.item;

import cn.nukkit.item.Item;

public class ItemSquidSpawnEgg extends ItemSpawnEgg {
    public ItemSquidSpawnEgg() {
        super(SQUID_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 17;
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}