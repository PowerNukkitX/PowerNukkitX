package cn.nukkit.item;

public class ItemBeeSpawnEgg extends ItemSpawnEgg {
    public ItemBeeSpawnEgg() {
        super(BEE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 132;
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}