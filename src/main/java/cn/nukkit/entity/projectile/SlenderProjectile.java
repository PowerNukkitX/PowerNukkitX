package cn.nukkit.entity.projectile;

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

/**
 * This abstract class represents slender projectile entities (e.g. arrows, tridents), and it realized a more accurate collision box calculation for these entities by overriding the {@link Entity#move} method.
 */
public abstract class SlenderProjectile extends EntityProjectile {
    private static final int SPLIT_NUMBER = 10;
    private MovingObjectPosition lastHitBlock;

    public SlenderProjectile(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public SlenderProjectile(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    /**
     * You should not set the width to a larger amount under normal circumstances. Use the default value if changing the width is not absolutely necessary.
     * @return The width of the projectile
     */
    @Override
    public float getWidth() {
        return 0.1f;
    }

    /**
     * You should not set the height to a larger amount under normal circumstances. Use the default value if changing the height is not absolutely necessary.
     * @return The height of the projectile
     */
    @Override
    public float getHeight() {
        return 0.1f;
    }

    // Testing has shown that this algorithm performs well in most cases.
    @Override
    public boolean move(double dx, double dy, double dz) {
        if (dx == 0 && dz == 0 && dy == 0) {
            return true;
        }

        this.ySize *= 0.4F;

        double movX = dx;
        double movY = dy;
        double movZ = dz;

        final SlenderProjectile projectile = this;

        var currentAABB = this.boundingBox.clone();
        var dirVector = new Vector3(dx, dy, dz).multiply(1 / (double) SPLIT_NUMBER);

        Entity collisionEntity = null;
        Block collisionBlock = null;
        for (int i = 0; i < SPLIT_NUMBER; ++i) {
            var collisionBlocks = this.level.getCollisionBlocks(currentAABB.offset(dirVector.x, dirVector.y, dirVector.z));
            var collisionEntities = this.getLevel().fastCollidingEntities(currentAABB, this);
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

        Vector3 centerPoint1 = new Vector3((currentAABB.getMinX() + currentAABB.getMaxX()) / 2,
                (currentAABB.getMinY() + currentAABB.getMaxY()) / 2,
                (currentAABB.getMinZ() + currentAABB.getMaxZ()) / 2);

        // Collide with entity
        if (collisionEntity != null) {
            MovingObjectPosition movingObject = new MovingObjectPosition();
            movingObject.typeOfHit = 1;
            movingObject.entityHit = collisionEntity;
            movingObject.hitVector = centerPoint1;
            ProjectileHitEvent event = new ProjectileHitEvent(this, movingObject);
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                onCollideWithEntity(movingObject.entityHit);
                return true;
            }
        }

        Vector3 centerPoint2 = new Vector3((this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2,
                (this.boundingBox.getMinY() + this.boundingBox.getMaxY()) / 2,
                (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2);

        Vector3 diff = centerPoint1.subtract(centerPoint2);
        double diffY = diff.getY();
        if (dy > 0 && diffY + 0.001 < dy
                || dy < 0 && diffY - 0.001 > dy) {
            dy = diffY;
        }

        double diffX = diff.getX();
        if (dx > 0 && diffX + 0.001 < dx
                || dx < 0 && diffX - 0.001 > dx) {
            dx = diffX;
        }

        double diffZ = diff.getZ();
        if (dz > 0 && diffZ + 0.001 < dz
                || dz < 0 && diffZ - 0.001 > dz) {
            dz = diffZ;
        }

        this.boundingBox.offset(dx, dy, dz);
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

        // Collide with block
        if (this.isCollided && !this.hadCollision) {
            this.hadCollision = true;
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;

            BVector3 bVector3 = BVector3.fromPos(new Vector3(dx, dy, dz));
            BlockFace blockFace = BlockFace.fromHorizontalAngle(bVector3.getYaw());
            Block block = level.getBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ()).getSide(blockFace);
            if (block.isAir()) { // If the block is air, search below instead
                blockFace = BlockFace.DOWN;
                block = level.getBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ()).down();
            }
            if (block.isAir()) { // If the block is air, search above instead
                blockFace = BlockFace.UP;
                block = level.getBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ()).up();
            }
            if (block.isAir() && collisionBlock != null) {
                block = collisionBlock;
            }

            ProjectileHitEvent event = new ProjectileHitEvent(this, lastHitBlock = MovingObjectPosition.fromBlock(block.getFloorX(), block.getFloorY(), block.getFloorZ(), blockFace, this));
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                onCollideWithBlock(getPosition(), getMotion(), block);
                addHitEffect();
            }
        }
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

        if (this.isCollided && this.hadCollision) {
            if (lastHitBlock != null && lastHitBlock.typeOfHit == 0 && level.getBlock(lastHitBlock.blockX, lastHitBlock.blockY, lastHitBlock.blockZ).isAir()) {
                this.motionY -= this.getGravity();
                updateRotation();
                this.move(this.motionX, this.motionY, this.motionZ);
                this.updateMovement();
            }
            return this.entityBaseTick(tickDiff);
        }

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {
            if (!this.isCollided) {
                updateMotion();
            }
            hasUpdate = this.checkCollisionAndUpdateRotation();
            this.move(this.motionX, this.motionY, this.motionZ);
            this.updateMovement();
        }
        return hasUpdate;
    }
}
