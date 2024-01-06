package cn.nukkit.item;

public class ItemElderGuardianSpawnEgg extends ItemSpawnEgg {
    public ItemElderGuardianSpawnEgg() {
        super(ELDER_GUARDIAN_SPAWN_EGG);
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getEntityNetworkId() {
        return 50;
    }
}