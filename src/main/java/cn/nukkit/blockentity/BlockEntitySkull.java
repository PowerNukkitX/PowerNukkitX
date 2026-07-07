package cn.nukkit.blockentity;

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
    private int skullType;
    private int rotation;

    @Override
    public void loadNBT() {
        super.loadNBT();

        if (!nbt.contains("SkullType")) {
            nbt.putByte("SkullType", (byte) 0);
        }
        if (!nbt.contains("Rot")) {
            nbt.putByte("Rot", (byte) 0);
        }

        this.skullType = this.nbt.getByte("SkullType") & 0xff;
        this.rotation = this.nbt.getByte("Rot") & 0x0f;

        if (nbt.contains("MouthMoving")) {
            mouthMoving = getNbt().getBoolean("MouthMoving");
        }

        if (nbt.contains("MouthTickCount")) {
            mouthTickCount = NukkitMath.clamp(getNbt().getInt("MouthTickCount"), 0, 60);
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
        setDirty();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.nbt.putByte("SkullType", (byte) this.skullType)
                .putByte("Rot", (byte) this.rotation)
                .putBoolean("MouthMoving", this.mouthMoving)
                .putInt("MouthTickCount", this.mouthTickCount);

        this.nbt.remove("Creator");
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock() instanceof BlockHead;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putByte("SkullType", (byte) this.skullType)
                .putByte("Rot", (byte) this.rotation)
                .putBoolean("MouthMoving", this.mouthMoving)
                .putInt("MouthTickCount", this.mouthTickCount);
    }
}
