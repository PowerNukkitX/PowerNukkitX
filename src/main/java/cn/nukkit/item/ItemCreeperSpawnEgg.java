package cn.nukkit.item;

public class ItemCreeperSpawnEgg extends ItemSpawnEgg {
    public ItemCreeperSpawnEgg() {
        super(CREEPER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 33;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}