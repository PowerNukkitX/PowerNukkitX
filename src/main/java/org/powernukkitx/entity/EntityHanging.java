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
        if (nbtMap.contains("Direction")) {
            this.direction = nbtMap.getByte("Direction");
        } else if (nbtMap.contains("Dir")) {
            int d = nbtMap.getByte("Dir");
            if (d == 2) {
                this.direction = 0;
            } else if (d == 0) {
                this.direction = 2;
            }
        }

    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putByte("Direction", (byte) this.getDirection().getHorizontalIndex())
                .putInt("TileX", (int) this.x)
                .putInt("TileY", (int) this.y)
                .putInt("TileZ", (int) this.z);
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
            this.direction = (int) (this.yaw / 90);
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
