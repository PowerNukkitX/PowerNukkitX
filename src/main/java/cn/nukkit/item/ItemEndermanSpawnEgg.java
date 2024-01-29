package cn.nukkit.item;

public class ItemEndermanSpawnEgg extends ItemSpawnEgg {
    public ItemEndermanSpawnEgg() {
        super(ENDERMAN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 38;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}