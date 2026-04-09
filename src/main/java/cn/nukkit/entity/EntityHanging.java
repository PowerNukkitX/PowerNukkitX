package cn.nukkit.entity;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import org.cloudburstmc.nbt.NbtMap;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityHanging extends Entity {
    protected int direction;

    public EntityHanging(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setHealthMax(1);
        this.setHealthCurrent(1);

        if (this.namedTag.containsKey("Direction")) {
            this.direction = this.namedTag.getByte("Direction");
        } else if (this.namedTag.containsKey("Dir")) {
            int d = this.namedTag.getByte("Dir");
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
        this.namedTag = this.namedTag.toBuilder()
                .putByte("Direction", (byte) this.getDirection().getHorizontalIndex())
                .putInt("TileX", (int) this.x)
                .putInt("TileY", (int) this.y)
                .putInt("TileZ", (int) this.z)
                .build();
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
