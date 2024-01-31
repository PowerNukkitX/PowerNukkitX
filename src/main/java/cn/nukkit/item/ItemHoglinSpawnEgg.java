package cn.nukkit.item;

public class ItemHoglinSpawnEgg extends ItemSpawnEgg {
    public ItemHoglinSpawnEgg() {
        super(HOGLIN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 124;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}