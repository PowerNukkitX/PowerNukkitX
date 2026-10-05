package org.powernukkitx.item;

import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class ItemSplashPotion extends ProjectileItem {
    public ItemSplashPotion() {
        this(0, 1);
    }

    public ItemSplashPotion(Integer meta) {
        this(meta, 1);
    }

    public ItemSplashPotion(Integer meta, int count) {
        super(SPLASH_POTION, meta, count, "Splash Potion");
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
            name = "Splash Water Bottle";
        } else {
            name = ItemPotion.buildName(potion, "Splash Potion", true);
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
        return SPLASH_POTION;
    }

    @Override
    public float getThrowForce() {
        return 0.5f;
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putInt("PotionId", this.meta);
    }

    /**
     * Creates a splash potion item containing the specified potion type.
     *
     * @param potion the potion type stored in the new item
     * @return a new splash potion item with a count of one
     * @throws NullPointerException if {@code potion} is {@code null}
     */
    @NotNull
    public static ItemSplashPotion fromPotion(@NotNull PotionType potion) {
        return new ItemSplashPotion(potion.id());
    }
}
