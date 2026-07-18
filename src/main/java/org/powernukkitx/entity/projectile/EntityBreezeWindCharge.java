package org.powernukkitx.entity.projectile;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;


public class EntityBreezeWindCharge extends EntityWindCharge {


    public EntityBreezeWindCharge(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityBreezeWindCharge(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public @NotNull String getIdentifier() {
        return BREEZE_WIND_CHARGE_PROJECTILE;
    }

    @Override
    public String getOriginalName() {
        return "Breeze Wind Charge Projectile";
    }

    @Override
    public double getBurstRadius() {
        return 3f;
    }

    @Override
    public double getKnockbackStrength() {
        return 0.18f;
    }
}
