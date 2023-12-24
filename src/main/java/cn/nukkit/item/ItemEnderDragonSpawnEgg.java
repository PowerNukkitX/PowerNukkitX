package cn.nukkit.item;

public class ItemEnderDragonSpawnEgg extends ItemSpawnEgg {
    public ItemEnderDragonSpawnEgg() {
        super(ENDER_DRAGON_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}