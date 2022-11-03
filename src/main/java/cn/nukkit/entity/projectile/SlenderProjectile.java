package cn.nukkit.entity.projectile;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import co.aikar.timings.Timings;

/**
 * 这个抽象类代表较为细长的投射物实体(例如弓箭,三叉戟),它通过重写{@link Entity#move}方法实现这些实体较为准确的碰撞箱计算。
 * <p>
 * This abstract class represents slender projectile entities (e.g.arrow, trident), and it realized a more accurate collision box calculation for these entities by overriding the {@link Entity#move} method.
 */
public abstract class SlenderProjectile extends EntityProjectile {

    public SlenderProjectile(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public SlenderProjectile(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    //对于SlenderProjectile你不应该把Width设置太大,如果没必要请使用默认值.
    @Override
    public float getWidth() {
        return 0.1f;
    }

    //对于SlenderProjectile你不应该把Height设置太大,如果没必要请使用默认值.
    @Override
    public float getHeight() {
        return 0.1f;
    }

    /*
     * 经过测试这个算法在大多数情况下效果不错，但是在部分情况下，碰撞发生后会出现箭插入过深，或奇怪的向下偏移几格落入土里。
     */
    @Override
    public boolean move(double dx, double dy, double dz) {
        if (dx == 0 && dz == 0 && dy == 0) {
            return true;
        }
        Timings.entityMoveTimer.startTiming();

        this.ySize *= 0.4;

        double movX = dx;
        double movY = dy;
        double movZ = dz;

        var currentAABB = this.boundingBox.clone();
        var dirVector = new Vector3(dx, dy, dz).multiply(1 / (double) 10);
        boolean isCollision = false;
        for (int i = 0; i < 10; ++i) {
            var collisionResult = this.level.fastCollisionCubes(this, currentAABB.offset(dirVector.x, dirVector.y, dirVector.z), false);
            if (!collisionResult.isEmpty()) {
                isCollision = true;
                break;
            }
        }
        //todo 改进这个dx dy dz计算
        if (isCollision) {
            if (dy > 0 && this.boundingBox.getMaxY() <= currentAABB.getMinY()) {
                double y1 = currentAABB.getMinY() - this.boundingBox.getMaxY();
                if (y1 < dy) {
                    dy = y1;
                }
            }
            if (dy < 0 && this.boundingBox.getMinY() >= currentAABB.getMaxY()) {
                double y2 = currentAABB.getMaxY() - this.boundingBox.getMinY();
                if (y2 > dy) {
                    dy = y2;
                }
            }

            if (dx > 0 && this.boundingBox.getMaxX() <= currentAABB.getMinX()) {
                double x1 = currentAABB.getMinX() - this.boundingBox.getMaxX();
                if (x1 < dx) {
                    dx = x1;
                }
            }
            if (dx < 0 && this.boundingBox.getMinX() >= currentAABB.getMaxX()) {
                double x2 = currentAABB.getMaxX() - this.boundingBox.getMinX();
                if (x2 > dx) {
                    dx = x2;
                }
            }

            if (dz > 0 && this.boundingBox.getMaxZ() <= currentAABB.getMinZ()) {
                double z1 = currentAABB.getMinZ() - this.boundingBox.getMaxZ();
                if (z1 < dz) {
                    dz = z1;
                }
            }
            if (dz < 0 && this.boundingBox.getMinZ() >= currentAABB.getMaxZ()) {
                double z2 = currentAABB.getMaxZ() - this.boundingBox.getMinZ();
                if (z2 > dz) {
                    dz = z2;
                }
            }
        }
        this.boundingBox.offset(0, dy, 0);
        this.boundingBox.offset(dx, 0, 0);
        this.boundingBox.offset(0, 0, dz);
        this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
        this.y = this.boundingBox.getMinY() - this.ySize;
        this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

        this.checkChunks();

        this.checkGroundState(movX, movY, movZ, dx, dy, dz);
        this.updateFallState(this.onGround);

        if (movX != dx) {
            this.motionX = 0;
        }

        if (movY != dy) {
            this.motionY = 0;
        }

        if (movZ != dz) {
            this.motionZ = 0;
        }

        //TODO: vehicle collision events (first we need to spawn them!)
        Timings.entityMoveTimer.stopTiming();
        return true;
    }
}
