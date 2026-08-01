package org.powernukkitx.item;

import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.nbt.tag.CompoundTag;

public class ItemSplashPotion extends ProjectileItem {
    public static final ItemDefinition DEFINITION = ProjectileItem.DEFINITION.toBuilder()
            .canBeActivated(true)
            .maxStackSize(1)
            .build();

    public ItemSplashPotion() {
        this(0, 1);
    }

    public ItemSplashPotion(Integer meta) {
        this(meta, 1);
    }

    public ItemSplashPotion(Integer meta, int count) {
        super(SPLASH_POTION, meta, count, "Splash Potion", DEFINITION);
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
}
