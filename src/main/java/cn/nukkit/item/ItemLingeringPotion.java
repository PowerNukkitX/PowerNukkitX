package cn.nukkit.item;

import cn.nukkit.entity.effect.PotionType;
import cn.nukkit.nbt.tag.CompoundTag;

public class ItemLingeringPotion extends ProjectileItem {

    public ItemLingeringPotion() {
        this(0, 1);
    }

    public ItemLingeringPotion(Integer meta) {
        this(meta, 1);
    }

    public ItemLingeringPotion(Integer meta, int count) {
        super(LINGERING_POTION, meta, count, "Lingering Potion");
        updateName();
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
        updateName();
    }

    private void updateName() {
        PotionType potion = PotionType.get(getDamage());
        if (PotionType.WATER.equals(potion)) {
            name = "Lingering Water Bottle";
        } else {
            name = ItemPotion.buildName(potion, "Lingering Potion", true);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public String getProjectileEntityType() {
        return LINGERING_POTION;
    }

    @Override
    public float getThrowForce() {
        return 0.5f;
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putInt("PotionId", this.meta);
    }
}