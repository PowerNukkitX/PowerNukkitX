package cn.nukkit.blockentity;

import cn.nukkit.block.BlockHead;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.NbtHelper;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

/**
 * @author Snake1999
 * @since 2016/2/3
 */
public class BlockEntitySkull extends BlockEntitySpawnable {
    public BlockEntitySkull(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    private boolean mouthMoving;

    private int mouthTickCount;


    @Override
    public void loadNBT() {
        super.loadNBT();
        NbtMapBuilder builder = this.namedTag.toBuilder();
        if (!namedTag.containsKey("SkullType")) {
            builder.putByte("SkullType", (byte) 0);
        }
        if (!namedTag.containsKey("Rot")) {
            builder.putByte("Rot", (byte) 0);
        }

        this.namedTag = builder.build();

        if (namedTag.containsKey("MouthMoving")) {
            mouthMoving = namedTag.getBoolean("MouthMoving");
        }

        if (namedTag.containsKey("MouthTickCount")) {
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
        this.namedTag = this.namedTag.toBuilder()
                .putBoolean("MouthMoving", this.mouthMoving)
                .putInt("MouthTickCount", mouthTickCount)
                .build();
        this.namedTag = NbtHelper.remove(this.namedTag, "Creator");
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock() instanceof BlockHead;
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putByte("SkullType", (Byte) this.namedTag.get("SkullType"))
                .putByte("Rot", (Byte) this.namedTag.get("Rot"))
                .putBoolean("MouthMoving", this.mouthMoving)
                .putInt("MouthTickCount", mouthTickCount)
                .build();
    }
}