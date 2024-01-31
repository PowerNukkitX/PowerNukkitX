package cn.nukkit.item;

public class ItemPufferfishSpawnEgg extends ItemSpawnEgg {
    public ItemPufferfishSpawnEgg() {
        super(PUFFERFISH_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 108;
    }

    @Override
    public void setDamage(int meta) {

    }
}