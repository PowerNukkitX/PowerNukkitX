package org.powernukkitx.item;

/**
 * Thrown like a snowball. The launch power is the one the item's own throwable component asks for.
 */
public class ItemIceBall extends ProjectileItem {

    public ItemIceBall() {
        this(0, 1);
    }

    public ItemIceBall(Integer meta) {
        this(meta, 1);
    }

    public ItemIceBall(Integer meta, int count) {
        super(ICE_BALL, 0, count, "Ice Ball");
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public String getProjectileEntityType() {
        return ICE_BALL;
    }

    @Override
    public float getThrowForce() {
        return 1.0f;
    }
}
