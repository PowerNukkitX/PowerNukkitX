package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.ByteTag;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;

/**
 * Component defining explosion resistance.
 */
public class DestructibleByExplosionComponent implements BlockComponent {
    private int explosionResistance = 0;

    public DestructibleByExplosionComponent() {
    }

    /**
     * Set explosion resistance. Use -1 for indestructible.
     */
    public DestructibleByExplosionComponent explosionResistance(int resistance) {
        this.explosionResistance = resistance;
        return this;
    }

    /**
     * Convenience method: true = destructible (resistance 0), false = indestructible.
     */
    public DestructibleByExplosionComponent destructible(boolean destructible) {
        this.explosionResistance = destructible ? 0 : -1;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.DESTRUCTIBLE_BY_EXPLOSION;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag().putInt("explosion_resistance", explosionResistance);
    }
}