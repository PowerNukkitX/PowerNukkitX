package cn.nukkit.item;

public class ItemZombieHorseSpawnEgg extends ItemSpawnEgg {
    public ItemZombieHorseSpawnEgg() {
        super(ZOMBIE_HORSE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 27;
    }

    @Override
    public void setDamage(int meta) {

    }
}