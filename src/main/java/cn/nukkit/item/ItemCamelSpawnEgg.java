package cn.nukkit.item;

public class ItemCamelSpawnEgg extends ItemSpawnEgg {
    public ItemCamelSpawnEgg() {
        super(CAMEL_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 138;
    }

    @Override
    public void setDamage(int meta) {

    }
}