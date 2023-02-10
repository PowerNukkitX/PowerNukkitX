package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDaylightDetector;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitOnly
public class BlockEntityDaylightDetector extends BlockEntity {

    @PowerNukkitOnly
    public BlockEntityDaylightDetector(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        scheduleUpdate();
    }

    @Override
    public boolean isBlockEntityValid() {
        return getLevelBlock().getId() == BlockID.DAYLIGHT_DETECTOR;
    }

    @Override
    public boolean onUpdate() {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return false;
        }
        if (this.level.getCurrentTick() % 20 != 0) {
            //阳光传感器每20gt更新一次
            return true;
        }
        Block block = getLevelBlock();
        if (block instanceof BlockDaylightDetector b) {
            b.updatePower();
            return true;
        } else {
            return false;
        }
    }

}
