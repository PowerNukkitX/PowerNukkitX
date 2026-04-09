package cn.nukkit.item;

import cn.nukkit.entity.effect.PotionType;
import org.cloudburstmc.nbt.NbtMap;

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
    protected void correctNBT(NbtMap nbt) {
        nbt = nbt.toBuilder().putInt("PotionId", this.meta).build();
    }
}