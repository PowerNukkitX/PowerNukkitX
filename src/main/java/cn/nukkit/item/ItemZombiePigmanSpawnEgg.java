package cn.nukkit.item;

public class ItemZombiePigmanSpawnEgg extends ItemSpawnEgg {
    public ItemZombiePigmanSpawnEgg() {
        super(ZOMBIE_PIGMAN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 36;
    }

    @Override
    public void setDamage(int meta) {

    }
}