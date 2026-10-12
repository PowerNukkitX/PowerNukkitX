package org.powernukkitx.block.dispenser;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.projectile.EntityArrow;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemArrow;

public class ArrowDispenseBehavior extends ProjectileDispenseBehavior {

    public ArrowDispenseBehavior() {
        super(EntityID.ARROW);
    }

    @Override
    protected double getMotion() {
        return super.getMotion() * 1.5;
    }

    @Override
    protected void configureProjectile(Entity projectile, Item item) {
        if (projectile instanceof EntityArrow arrow && item instanceof ItemArrow arrowItem) {
            ItemArrow carried = (ItemArrow) arrowItem.clone();
            carried.setCount(1);
            arrow.setItem(carried);
        }
    }
}
