package org.powernukkitx;

import com.google.common.base.Preconditions;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Groups the canonical, PNX extra and custom persistent NBT roots of a player.
 *
 * @author Curse
 */
public final class PlayerNBTData {
    private final CompoundTag nbt;
    private final CompoundTag nbtExtra;
    private final CompoundTag nbtCustom;

    public PlayerNBTData(CompoundTag nbt, CompoundTag nbtExtra, CompoundTag nbtCustom) {
        this.nbt = Preconditions.checkNotNull(nbt, "nbt");
        this.nbtExtra = Preconditions.checkNotNull(nbtExtra, "nbtExtra");
        this.nbtCustom = Preconditions.checkNotNull(nbtCustom, "nbtCustom");
    }

    /**
     * Returns the canonical BDS-compatible player NBT.
     */
    public CompoundTag getNbt() {
        return this.nbt;
    }

    /**
     * Returns the PNX-specific player NBT sidecar.
     */
    public CompoundTag getNbtExtra() {
        return this.nbtExtra;
    }

    /**
     * Returns the custom player NBT sidecar.
     */
    public CompoundTag getNbtCustom() {
        return this.nbtCustom;
    }
}
