package cn.nukkit.item;

public class ItemRabbitSpawnEgg extends ItemSpawnEgg {
    public ItemRabbitSpawnEgg() {
        super(RABBIT_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 18;
    }

    @Override
    public void setDamage(int meta) {

    }
}