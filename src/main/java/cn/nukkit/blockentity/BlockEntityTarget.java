package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author joserobjr
 */


public class BlockEntityTarget extends BlockEntity {


    public BlockEntityTarget(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.TARGET;
    }

    public void setActivePower(int power) {
        namedTag.putInt("activePower", power);
    }

    public int getActivePower() {
        return NukkitMath.clamp(namedTag.getInt("activePower"), 0, 15);
    }
}
