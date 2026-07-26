package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * @author joserobjr
 */


public class BlockEntityTarget extends BlockEntity {


    public BlockEntityTarget(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return BlockID.TARGET.equals(getLevelBlock().getId());
    }

    public void setActivePower(int power) {
        this.nbt.putInt("activePower", power);
    }

    public int getActivePower() {
        return NukkitMath.clamp(getNbt().getInt("activePower"), 0, 15);
    }
}
