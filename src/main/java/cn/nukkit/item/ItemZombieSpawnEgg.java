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
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}