package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import co.aikar.timings.Timings;

/**
 * 这个抽象类代表较为细长的投射物实体(例如弓箭,三叉戟),它通过重写{@link Entity#move}方法实现这些实体较为准确的碰撞箱计算。
 * <p>
 * This abstract class represents slender projectile entities (e.g.arrow, trident), and it realized a more accurate collision box calculation for these entities by overriding the {@link Entity#move} method.
 */
public abstract class SlenderProjectile extends EntityProjectile {
    private static final int SPLIT_NUMBER = 15;

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
     * 经过测试这个算法在大多数情况下效果不错。
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
        var dirVector = new Vector3(dx, dy, dz).multiply(1 / (double) SPLIT_NUMBER);
        for (int i = 0; i < SPLIT_NUMBER; ++i) {
            var collisionResult = this.level.fastCollisionCubes(this, currentAABB.offset(dirVector.x, dirVector.y, dirVector.z), false);
            if (!collisionResult.isEmpty()) {
                break;
            }
        }
        Vector3 centerPoint1 = new Vector3((currentAABB.getMinX() + currentAABB.getMaxX()) / 2,
                (currentAABB.getMinY() + currentAABB.getMaxY()) / 2,
                (currentAABB.getMinZ() + currentAABB.getMaxZ()) / 2);
        Vector3 centerPoint2 = new Vector3((this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2,
                (this.boundingBox.getMinY() + this.boundingBox.getMaxY()) / 2,
                (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2);
        Vector3 diff = centerPoint1.subtract(centerPoint2);
        if (dy > 0) {
            if (diff.getY() + 0.001 < dy) {
                dy = diff.getY();
            }
        }
        if (dy < 0) {
            if (diff.getY() - 0.001 > dy) {
                dy = diff.getY();
            }
        }

        if (dx > 0) {
            if (diff.getX() + 0.001 < dx) {
                dx = diff.getX();
            }
        }
        if (dx < 0) {
            if (diff.getX() - 0.001 > dx) {
                dx = diff.getX();
            }
        }

        if (dz > 0) {
            if (diff.getZ() + 0.001 < dz) {
                dz = diff.getZ();
            }
        }
        if (dz < 0) {
            if (diff.getZ() - 0.001 > dz) {
                dz = diff.getZ();
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

        Timings.entityMoveTimer.stopTiming();
        return true;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            if (!this.isCollided) {
                updateMotion();
            }
            if (!this.hadCollision || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                updateRotation();
                hasUpdate = true;
            }
            var old = this.clone();
            this.move(this.motionX, this.motionY, this.motionZ);
            this.updateMovement();

            Entity[] list = this.getLevel().getCollidingEntities(this.boundingBox.grow(1, 1, 1), this);
            double nearDistance = Integer.MAX_VALUE;
            Entity nearEntity = null;
            MovingObjectPosition movingObjectPosition = null;
            for (Entity entity : list) {
                if ((entity == this.shootingEntity && this.ticksLived < 5) ||
                        (entity instanceof Player && ((Player) entity).getGamemode() == Player.SPECTATOR)) {
                    continue;
                }
                AxisAlignedBB axisalignedbb = entity.boundingBox.grow(0.3, 0.3, 0.3);
                MovingObjectPosition ob = axisalignedbb.calculateIntercept(old, this);
                if (ob == null) {
                    continue;
                }
                double distance = this.distanceSquared(ob.hitVector);
                if (distance < nearDistance) {
                    nearDistance = distance;
                    nearEntity = entity;
                }
            }
            if (nearEntity != null) {
                movingObjectPosition = MovingObjectPosition.fromEntity(nearEntity);
            }
            if (movingObjectPosition != null) {
                if (movingObjectPosition.entityHit != null) {
                    onCollideWithEntity(movingObjectPosition.entityHit);
                    hasUpdate = true;
                    if (closed) {
                        return true;
                    }
                }
            }

            Position position = getPosition();
            Vector3 motion = getMotion();
            if (this.isCollided && !this.hadCollision) { //collide with block
                this.hadCollision = true;
                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;
                this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ(), -1, this)));
                onCollideWithBlock(position, motion);
                addHitEffect();
                return false;
            } else if (!this.isCollided && this.hadCollision) {
                this.hadCollision = false;
            }
        }
        return hasUpdate;
    }
}
