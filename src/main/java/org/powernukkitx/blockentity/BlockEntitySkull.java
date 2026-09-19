package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockHead;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.nbt.tag.CompoundTag;

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
    private int skullType;
    private float rotation;

    @Override
    public void loadNBT() {
        super.loadNBT();

        this.skullType = this.nbt.getByte("SkullType") & 0xff;
        this.rotation = this.nbt.getFloat("Rotation");
        this.mouthMoving = this.nbt.getBoolean("DoingAnimation");
        this.mouthTickCount = NukkitMath.clamp(this.nbt.getInt("MouthTickCount"), 0, 60);
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
        setDirty();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.nbt.putByte("SkullType", (byte) this.skullType)
                .putFloat("Rotation", this.rotation)
                .putBoolean("DoingAnimation", this.mouthMoving)
                .putInt("MouthTickCount", this.mouthTickCount);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock() instanceof BlockHead;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putByte("SkullType", (byte) this.skullType)
                .putFloat("Rotation", this.rotation)
                .putBoolean("DoingAnimation", this.mouthMoving)
                .putInt("MouthTickCount", this.mouthTickCount);
    }
}
