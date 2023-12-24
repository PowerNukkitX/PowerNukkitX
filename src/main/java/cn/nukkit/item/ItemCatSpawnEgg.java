package cn.nukkit.item;

public class ItemCatSpawnEgg extends ItemSpawnEgg {
    public ItemCatSpawnEgg() {
        super(CAT_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 75;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}