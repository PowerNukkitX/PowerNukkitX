package cn.nukkit.item;

import cn.nukkit.item.Item;

public class ItemStriderSpawnEgg extends ItemSpawnEgg {
    public ItemStriderSpawnEgg() {
        super(STRIDER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 125;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}