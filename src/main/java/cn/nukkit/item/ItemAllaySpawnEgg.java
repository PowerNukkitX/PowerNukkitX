package cn.nukkit.item;

public class ItemAllaySpawnEgg extends ItemSpawnEgg {
    public ItemAllaySpawnEgg() {
        super(ALLAY_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 134;
    }

    @Override
    public void setDamage(int meta) {

    }
}