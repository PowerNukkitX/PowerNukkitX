package cn.nukkit.item;

public class ItemWardenSpawnEgg extends ItemSpawnEgg {
    public ItemWardenSpawnEgg() {
        super(WARDEN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 131;
    }

    @Override
    public void setDamage(int meta) {

    }
}