package cn.nukkit.item;

public class ItemHuskSpawnEgg extends ItemSpawnEgg {
    public ItemHuskSpawnEgg() {
        super(HUSK_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 47;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}