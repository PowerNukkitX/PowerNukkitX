package cn.nukkit.item;

public class ItemPiglinSpawnEgg extends ItemSpawnEgg {
    public ItemPiglinSpawnEgg() {
        super(PIGLIN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 123;
    }

    @Override
    public void setDamage(int meta) {

    }
}