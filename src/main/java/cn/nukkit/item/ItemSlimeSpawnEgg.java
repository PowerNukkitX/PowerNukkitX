package cn.nukkit.item;

public class ItemSlimeSpawnEgg extends ItemSpawnEgg {
    public ItemSlimeSpawnEgg() {
        super(SLIME_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 37;
    }

    @Override
    public void setDamage(int meta) {

    }
}