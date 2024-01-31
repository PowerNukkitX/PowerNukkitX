package cn.nukkit.item;

public class ItemZoglinSpawnEgg extends ItemSpawnEgg {
    public ItemZoglinSpawnEgg() {
        super(ZOGLIN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 126;
    }

    @Override
    public void setDamage(int meta) {

    }
}