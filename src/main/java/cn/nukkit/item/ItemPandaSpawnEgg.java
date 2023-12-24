package cn.nukkit.item;

public class ItemPandaSpawnEgg extends ItemSpawnEgg {
    public ItemPandaSpawnEgg() {
        super(PANDA_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 113;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}