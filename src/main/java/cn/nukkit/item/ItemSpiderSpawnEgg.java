package cn.nukkit.item;

import cn.nukkit.item.Item;

public class ItemSpiderSpawnEgg extends ItemSpawnEgg {
    public ItemSpiderSpawnEgg() {
        super(SPIDER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 35;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}