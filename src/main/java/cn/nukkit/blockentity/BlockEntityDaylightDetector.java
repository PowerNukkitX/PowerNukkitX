package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDaylightDetector;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;


public class BlockEntityDaylightDetector extends BlockEntity {


    public BlockEntityDaylightDetector(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        scheduleUpdate();
    }

    @Override
    public boolean isBlockEntityValid() {
        var id = getLevelBlock().getId();
        return id == BlockID.DAYLIGHT_DETECTOR || id == BlockID.DAYLIGHT_DETECTOR_INVERTED;
    }

    @Override
    public boolean onUpdate() {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
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
