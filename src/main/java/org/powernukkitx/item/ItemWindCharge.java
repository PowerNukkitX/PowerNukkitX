package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.projectile.EntityWindCharge;
import org.powernukkitx.item.definition.ItemDefinition;

import static org.powernukkitx.entity.EntityID.WIND_CHARGE_PROJECTILE;

public class ItemWindCharge extends ProjectileItem {
    public static final ItemDefinition DEFINITION = ProjectileItem.DEFINITION.toBuilder()
            .maxStackSize(64)
            .build();

    public ItemWindCharge() {
        this(0, 1);
    }

    public ItemWindCharge(Integer meta) {
        this(meta, 1);
    }

    public ItemWindCharge(Integer meta, int count) {
        super(WIND_CHARGE, 0, count, "Wind Charge", DEFINITION);
    }

    @Override
    public String getProjectileEntityType() {
        return WIND_CHARGE_PROJECTILE;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    protected Entity correctProjectile(Player player, Entity projectile) {
        if (projectile instanceof EntityWindCharge) {
            if (!player.isItemCoolDownEnd(this.getIdentifier())) {
                projectile.kill();
                return null;
            }
            player.setItemCoolDown(10, this.getIdentifier());
            return projectile;
        }
        return null;
    }
}