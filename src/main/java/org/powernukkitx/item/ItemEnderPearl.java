package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.projectile.EntityEnderPearl;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemEnderPearl extends ProjectileItem {
    public static final ItemDefinition DEFINITION = ProjectileItem.DEFINITION.toBuilder()
            .maxStackSize(16)
            .build();

    public ItemEnderPearl() {
        this(0, 1);
    }

    public ItemEnderPearl(Integer meta) {
        this(meta, 1);
    }

    public ItemEnderPearl(Integer meta, int count) {
        super(ENDER_PEARL, 0, count, "Ender Pearl", DEFINITION);
    }

    @Override
    public String getProjectileEntityType() {
        return ENDER_PEARL;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    protected Entity correctProjectile(Player player, Entity projectile) {
        if (projectile instanceof EntityEnderPearl) {
            if (!player.isItemCoolDownEnd(this.getIdentifier())) {
                projectile.kill();
                return null;
            }
            player.setItemCoolDown(20, this.getIdentifier());
            return projectile;
        }
        return null;
    }
}
