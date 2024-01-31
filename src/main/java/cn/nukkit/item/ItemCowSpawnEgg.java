package cn.nukkit.item;

public class ItemCowSpawnEgg extends ItemSpawnEgg {
    public ItemCowSpawnEgg() {
        super(COW_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 11;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}