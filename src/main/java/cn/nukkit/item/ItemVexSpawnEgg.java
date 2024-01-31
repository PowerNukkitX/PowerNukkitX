package cn.nukkit.item;

public class ItemVexSpawnEgg extends ItemSpawnEgg {
    public ItemVexSpawnEgg() {
        super(VEX_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 105;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}