package cn.nukkit.item;

public class ItemShulkerSpawnEgg extends ItemSpawnEgg {
    public ItemShulkerSpawnEgg() {
        super(SHULKER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 54;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}