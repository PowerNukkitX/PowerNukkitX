package cn.nukkit.item;

public class ItemZombieVillagerSpawnEgg extends ItemSpawnEgg {
    public ItemZombieVillagerSpawnEgg() {
        super(ZOMBIE_VILLAGER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 132;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}