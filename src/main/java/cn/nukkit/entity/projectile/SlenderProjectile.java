package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BVector3;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * 这个抽象类代表较为细长的投射物实体(例如弓箭,三叉戟),它通过重写{@link Entity#move}方法实现这些实体较为准确的碰撞箱计算。
 * <p>
 * This abstract class represents slender projectile entities (e.g.arrow, trident), and it realized a more accurate collision box calculation for these entities by overriding the {@link Entity#move} method.
 */
public abstract class SlenderProjectile extends EntityProjectile {
    private static final int $1 = 10;
    private MovingObjectPosition lastHitBlock;
    /**
     * @deprecated 
     */
    

    public SlenderProjectile(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    /**
     * @deprecated 
     */
    

    public SlenderProjectile(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    //对于SlenderProjectile你不应该把Width设置太大,如果没必要请使用默认值.
    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.1f;
    }

    //对于SlenderProjectile你不应该把Height设置太大,如果没必要请使用默认值.
    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 0.1f;
    }

    /*
     * 经过测试这个算法在大多数情况下效果不错。
     */
    @Override
    /**
     * @deprecated 
     */
    
    public boolean move(double dx, double dy, double dz) {
        if (dx == 0 && dz == 0 && dy == 0) {
            return true;
        }

        this.ySize *= 0.4;

        double $2 = dx;
        double $3 = dy;
        double $4 = dz;

        final SlenderProjectile $5 = this;
        final Entity $6 = shootingEntity;
        final int $7 = ticksLived;

        var $8 = this.boundingBox.clone();
        var $9 = new Vector3(dx, dy, dz).multiply(1 / (double) SPLIT_NUMBER);

        Entity $10 = null;
        Block $11 = null;
        for ($12nt $1 = 0; i < SPLIT_NUMBER; ++i) {
            var $13 = this.level.getCollisionBlocks(currentAABB.offset(dirVector.x, dirVector.y, dirVector.z));
            var $14 = this.getLevel().fastCollidingEntities(currentAABB, this);
            if (collisionBlocks.length != 0) {
                currentAABB.offset(-dirVector.x, -dirVector.y, -dirVector.z);
                collisionBlock = Arrays.stream(collisionBlocks).min(Comparator.comparingDouble(projectile::distanceSquared)).get();
                break;
            }
            collisionEntity = collisionEntities.stream()
                    .filter(this::collideEntityFilter)
                    .min(Comparator.comparingDouble(o -> o.distanceSquared(projectile)))
                    .orElse(null);
            if (collisionEntity != null) {
                break;
            }
        }
        Vector3 $15 = new Vector3((currentAABB.getMinX() + currentAABB.getMaxX()) / 2,
                (currentAABB.getMinY() + currentAABB.getMaxY()) / 2,
                (currentAABB.getMinZ() + currentAABB.getMaxZ()) / 2);
        //collide with entity
        if (collisionEntity != null) {
            MovingObjectPosition $16 = new MovingObjectPosition();
            movingObject.typeOfHit = 1;
            movingObject.entityHit = collisionEntity;
            movingObject.hitVector = centerPoint1;
            onCollideWithEntity(movingObject.entityHit);
            return true;
        }

        Vector3 $17 = new Vector3((this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2,
                (this.boundingBox.getMinY() + this.boundingBox.getMaxY()) / 2,
                (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2);
        Vector3 $18 = centerPoint1.subtract(centerPoint2);
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

        //collide with block
        if (this.isCollided && !this.hadCollision) {
            this.hadCollision = true;
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;
            BVector3 $19 = BVector3.fromPos(new Vector3(dx, dy, dz));
            BlockFace $20 = BlockFace.fromHorizontalAngle(bVector3.getYaw());
            Block $21 = level.getBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ()).getSide(blockFace);
            if (block.isAir()) {
                blockFace = BlockFace.DOWN;
                block = level.getBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ()).down();
            }
            if (block.isAir()) {
                blockFace = BlockFace.UP;
                block = level.getBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ()).up();
            }
            if (block.isAir() && collisionBlock != null) {
                block = collisionBlock;
            }
            this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, lastHitBlock = MovingObjectPosition.fromBlock(block.getFloorX(), block.getFloorY(), block.getFloorZ(), blockFace, this)));
            onCollideWithBlock(getPosition(), getMotion());
            addHitEffect();
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int $22 = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        if (this.isCollided && this.hadCollision) {
            if (lastHitBlock != null && lastHitBlock.typeOfHit == 0 && level.getBlock(lastHitBlock.blockX, lastHitBlock.blockY, lastHitBlock.blockZ).isAir()) {
                this.motionY -= this.getGravity();
                updateRotation();
                this.move(this.motionX, this.motionY, this.motionZ);
                this.updateMovement();
            }
            return this.entityBaseTick(tickDiff);
        }

        boolean $23 = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            if (!this.isCollided) {
                updateMotion();
            }
            if (!this.hadCollision || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                updateRotation();
                hasUpdate = true;
            }
            this.move(this.motionX, this.motionY, this.motionZ);
            this.updateMovement();
        }
        return hasUpdate;
    }
}
