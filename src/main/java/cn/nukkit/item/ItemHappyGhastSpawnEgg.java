package cn.nukkit.item;

public class ItemHappyGhastSpawnEgg extends ItemSpawnEgg {
    public ItemHappyGhastSpawnEgg() {
        super(HAPPY_GHAST_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 147;
    }

    @Override
    public void setDamage(int meta) {

    }
}