package cn.nukkit.item;

public class ItemOcelotSpawnEgg extends ItemSpawnEgg {
    public ItemOcelotSpawnEgg() {
        super(OCELOT_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 22;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}