package cn.nukkit.item;

public class ItemChickenSpawnEgg extends ItemSpawnEgg {
    public ItemChickenSpawnEgg() {
        super(CHICKEN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 10;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}