package cn.nukkit.item;

public class ItemTadpoleSpawnEgg extends ItemSpawnEgg {
    public ItemTadpoleSpawnEgg() {
        super(TADPOLE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 133;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}