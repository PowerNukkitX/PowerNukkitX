package cn.nukkit.item;

import cn.nukkit.item.Item;

public class ItemTadpoleSpawnEgg extends ItemSpawnEgg {
    public ItemTadpoleSpawnEgg() {
        super(TADPOLE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 133;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}