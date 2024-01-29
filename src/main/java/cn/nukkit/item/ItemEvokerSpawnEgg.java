package cn.nukkit.item;

public class ItemEvokerSpawnEgg extends ItemSpawnEgg {
    public ItemEvokerSpawnEgg() {
        super(EVOKER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 104;
    }

    @Override
    public void setDamage(int meta) {

    }
}