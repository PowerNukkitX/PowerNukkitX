package cn.nukkit.item;

import static cn.nukkit.entity.EntityID.WIND_CHARGE_PROJECTILE;

public class ItemWindCharge extends ProjectileItem {

    public ItemWindCharge() {
        this(0, 1);
    }

    public ItemWindCharge(Integer meta) {
        this(meta, 1);
    }

    public ItemWindCharge(Integer meta, int count) {
        super(WIND_CHARGE, 0, count, "Wind Charge");
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public String getProjectileEntityType() {
        return WIND_CHARGE_PROJECTILE;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }
}