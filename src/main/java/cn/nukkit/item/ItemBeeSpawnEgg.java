package cn.nukkit.item;

public class ItemBeeSpawnEgg extends ItemSpawnEgg {
    public ItemBeeSpawnEgg() {
        super(BEE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 122;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}