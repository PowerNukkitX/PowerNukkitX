package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemEgg extends ProjectileItem {

    public ItemEgg() {
        this(0, 1);
    }

    public ItemEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemEgg(Integer meta, int count) {
        super(EGG, meta, count, "Egg");
    }

    protected ItemEgg(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public String getProjectileEntityType() {
        return EGG;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    public int getMaxStackSize() { return 16; }
}
