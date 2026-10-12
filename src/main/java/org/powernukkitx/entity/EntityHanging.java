package org.powernukkitx.entity;

import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityHanging extends Entity {
    protected int direction;

    public EntityHanging(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setHealthMax(1);
        this.setHealthCurrent(1);

        final CompoundTag nbtMap = this.getNbt();
        this.direction = nbtMap.getByte("Direction");
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putByte("Direction", (byte) this.direction);
    }

    @Override
    public BlockFace getDirection() {
        return BlockFace.fromIndex(this.direction);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isPlayer) {
            this.blocksAround = null;
            this.collisionBlocks = null;
        }

        if (!this.isAlive()) {
            this.despawnFromAll();
            if (!this.isPlayer) {
                this.close();
            }
            return true;
        }

        this.checkBlockCollision();

        if (this.lastYaw != this.yaw || this.lastX != this.x || this.lastY != this.y || this.lastZ != this.z) {
            this.despawnFromAll();
            this.lastYaw = this.yaw;
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;
            this.spawnToAll();
            return true;
        }

        return false;
    }

    protected boolean isSurfaceValid() {
        return true;
    }

}
