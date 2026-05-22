package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import org.cloudburstmc.nbt.NbtMap;

/**
 * @author joserobjr
 */


public class BlockEntityTarget extends BlockEntity {


    public BlockEntityTarget(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.TARGET;
    }

    public void setActivePower(int power) {
        this.nbt.putInt("activePower", power);
    }

    public int getActivePower() {
        return NukkitMath.clamp(getNbt().getInt("activePower"), 0, 15);
    }
}
