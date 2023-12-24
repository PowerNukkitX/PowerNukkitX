package cn.nukkit.item;

public class ItemZombieSpawnEgg extends ItemSpawnEgg {
    public ItemZombieSpawnEgg() {
        super(ZOMBIE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 44;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}