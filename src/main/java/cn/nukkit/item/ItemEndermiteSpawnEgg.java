package cn.nukkit.item;

public class ItemEndermiteSpawnEgg extends ItemSpawnEgg {
    public ItemEndermiteSpawnEgg() {
        super(ENDERMITE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 55;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}