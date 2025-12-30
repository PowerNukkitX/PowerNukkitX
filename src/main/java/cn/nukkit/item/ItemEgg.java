package cn.nukkit.item;

import cn.nukkit.entity.ClimateVariant;
import cn.nukkit.nbt.tag.CompoundTag;

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

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putString("variant", ClimateVariant.Variant.TEMPERATE.getName());
    }
}
