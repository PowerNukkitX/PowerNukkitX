package cn.nukkit.item;

public class ItemWitchSpawnEgg extends ItemSpawnEgg {
    public ItemWitchSpawnEgg() {
        super(WITCH_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 45;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}