package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockHead;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Snake1999
 * @since 2016/2/3
 */
public class BlockEntitySkull extends BlockEntitySpawnable {
    public BlockEntitySkull(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    
    private boolean mouthMoving;
    
    private int mouthTickCount;


    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!namedTag.contains("SkullType")) {
            namedTag.putByte("SkullType", 0);
        }
        if (!namedTag.contains("Rot")) {
            namedTag.putByte("Rot", 0);
        }

        if (namedTag.containsByte("MouthMoving")) {
            mouthMoving = namedTag.getBoolean("MouthMoving");
        }

        if (namedTag.containsInt("MouthTickCount")) {
            mouthTickCount = NukkitMath.clamp(namedTag.getInt("MouthTickCount"), 0, 60);
        }
    }

    @Override
    public boolean onUpdate() {
        if (isMouthMoving()) {
            mouthTickCount++;
            setDirty();
            return true;
        }
        return false;
    }

    public void setMouthMoving(boolean mouthMoving) {
        if (this.mouthMoving == mouthMoving) {
            return;
        }
        this.mouthMoving = mouthMoving;
        if (mouthMoving) {
            scheduleUpdate();
        }
        this.level.updateComparatorOutputLevelSelective(this, true);
        spawnToAll();
        if (chunk != null) {
            setDirty();
        }
    }

    @Override
    public boolean isObservable() {
        return false;
    }

    @Override
    public void setDirty() {
        chunk.setChanged();
    }

    public boolean isMouthMoving() {
        return mouthMoving;
    }

    public int getMouthTickCount() {
        return mouthTickCount;
    }

    public void setMouthTickCount(int mouthTickCount) {
        if (this.mouthTickCount == mouthTickCount) {
            return;
        }
        this.mouthTickCount = mouthTickCount;
        spawnToAll();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag
                .putBoolean("MouthMoving", this.mouthMoving)
                .putInt("MouthTickCount", mouthTickCount)
                .remove("Creator");
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock() instanceof BlockHead;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .put("SkullType", this.namedTag.get("SkullType"))
                .put("Rot", this.namedTag.get("Rot"))
                .putBoolean("MouthMoving", this.mouthMoving)
                .putInt("MouthTickCount", mouthTickCount);
    }

}
