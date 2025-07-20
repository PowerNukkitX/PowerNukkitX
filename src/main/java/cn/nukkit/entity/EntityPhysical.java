package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingLava;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.EntityFreezeEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class EntityPhysical extends EntityCreature implements EntityAsyncPrepare {
    /**
     * Movement accuracy threshold. Movements with an absolute value less than this threshold are considered as no movement.
     */
    public static final float PRECISION = 0.00001f;

    public static final AtomicInteger globalCycleTickSpread = new AtomicInteger();
    /**
     * Time flooding delay is used to alleviate the situation where a large number of tasks are submitted at the same time and occupy the CPU.
     */
    public final int tickSpread;
    /**
     * Provide real-time latest collision box position
     */
    protected final AxisAlignedBB offsetBoundingBox;
    protected final Vector3 previousCollideMotion;
    protected final Vector3 previousCurrentMotion;
    /**
     * The time of free fall of an object
     */
    protected int fallingTick = 0;
    protected boolean needsRecalcMovement = true;
    private boolean needsCollisionDamage = false;

    public EntityPhysical(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.tickSpread = globalCycleTickSpread.getAndIncrement() & 0xf;
        this.offsetBoundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
        previousCollideMotion = new Vector3();
        previousCurrentMotion = new Vector3();
    }

    @Override
    public void asyncPrepare(int currentTick) {
        // Calculates whether expensive entity motion needs to be recalculated
        this.needsRecalcMovement = this.level.tickRateOptDelay == 1 || ((currentTick + tickSpread) & (this.level.tickRateOptDelay - 1)) == 0;
        // Recalculate absolute position collision box
        this.calculateOffsetBoundingBox();
        if (!this.isImmobile()) {
            // Dealing with gravity
            handleGravity();
            if (needsRecalcMovement) {
                // Handling collision box extrusion movement
                handleCollideMovement(currentTick);
            }
            addTmpMoveMotionXZ(previousCollideMotion);
            handleFloatingMovement();
            handleGroundFrictionMovement();
            handlePassableBlockFrictionMovement();
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        // Record the maximum height for calculating fall damage
        if (!this.onGround && this.y > highestPosition) {
            this.highestPosition = this.y;
        }
        // Added crush damage
        if (needsCollisionDamage) {
            this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.COLLIDE, 3));
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = super.entityBaseTick(tickDiff);
        //handle human entity freeze
        var collidedWithPowderSnow = this.getTickCachedCollisionBlocks().stream().anyMatch(block -> block.getId() == Block.POWDER_SNOW);
        if (this.getFreezingTicks() < 140 && collidedWithPowderSnow) {
            this.addFreezingTicks(1);
            EntityFreezeEvent event = new EntityFreezeEvent(this);
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                //this.setMovementSpeed(); //todo 给物理实体添加freeze减速
            }
        } else if (this.getFreezingTicks() > 0 && !collidedWithPowderSnow) {
            this.addFreezingTicks(-1);
            //this.setMovementSpeed();
        }
        if (this.getFreezingTicks() == 140 && this.getLevel().getTick() % 40 == 0) {
            this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.FREEZING, getFrostbiteInjury()));
        }
        return hasUpdate;
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return true;
    }

    @Override
    public void updateMovement() {
        // Detection of free fall time
        if (isFalling()) {
            this.fallingTick++;
        }
        super.updateMovement();
        this.move(this.motionX, this.motionY, this.motionZ);
    }

    public boolean isFalling() {
        return !this.onGround && this.y < this.highestPosition;
    }

    public final void addTmpMoveMotion(Vector3 tmpMotion) {
        this.motionX += tmpMotion.x;
        this.motionY += tmpMotion.y;
        this.motionZ += tmpMotion.z;
    }

    public final void addTmpMoveMotionXZ(Vector3 tmpMotion) {
        this.motionX += tmpMotion.x;
        this.motionZ += tmpMotion.z;
    }

    protected void handleGravity() {
        // Gravity is always there
        this.motionY -= this.getGravity();
        if (!this.onGround && this.hasWaterAt(getFootHeight())) {
            // Landing water
            resetFallDistance();
        }
    }

    /**
     * Calculating ground friction
     */

    protected void handleGroundFrictionMovement() {
        // No ground resistance
        if (!this.onGround) return;
        // Less than precision
        if (Math.abs(this.motionZ) < PRECISION && Math.abs(this.motionX) < PRECISION) return;
        // Reduce movement vector (calculate friction coefficient, slide further on ice)
        final double factor = getGroundFrictionFactor();
        this.motionX *= factor;
        this.motionZ *= factor;

        if (Math.abs(this.motionX) < PRECISION) this.motionX = 0;
        if (Math.abs(this.motionZ) < PRECISION) this.motionZ = 0;
    }

    /**
     * Calculate fluid resistance (air/liquid)
     */

    protected void handlePassableBlockFrictionMovement() {
        // Less than precision
        if (Math.abs(this.motionZ) < PRECISION && Math.abs(this.motionX) < PRECISION && Math.abs(this.motionY) < PRECISION)
            return;
        final double factor = getPassableBlockFrictionFactor();
        this.motionX *= factor;
        this.motionY *= factor;
        this.motionZ *= factor;

        if (Math.abs(this.motionX) < PRECISION) this.motionX = 0;
        if (Math.abs(this.motionY) < PRECISION) this.motionY = 0;
        if (Math.abs(this.motionZ) < PRECISION) this.motionZ = 0;
    }

    /**
     * Calculate the ground friction factor at the current location
     *
     * @return The ground friction factor at the current location
     */

    public double getGroundFrictionFactor() {
        if (!this.onGround) return 1.0;
        return this.getLevel().getTickCachedBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z))).getFrictionFactor();
    }

    /**
     * Calculate the fluid resistance factor (air/water) at the current location
     *
     * @return The fluid resistance factor at the current location
     */

    public double getPassableBlockFrictionFactor() {
        var block = this.getTickCachedLevelBlock();
        if (block.collidesWithBB(this.getBoundingBox(), true)) return block.getPassableBlockFrictionFactor();
        return Block.DEFAULT_AIR_FLUID_FRICTION;
    }

    /**
     * By default, the built-in implementation of nk is used, which is just a fallback algorithm.
     */
    protected void handleLiquidMovement() {
        final var tmp = new Vector3();
        BlockLiquid blockLiquid = null;
        for (final var each : this.getLevel().getCollisionBlocks(getOffsetBoundingBox(),
                false, true, block -> block instanceof BlockLiquid)) {
            blockLiquid = (BlockLiquid) each;
            final var flowVector = blockLiquid.getFlowVector();
            tmp.x += flowVector.x;
            tmp.y += flowVector.y;
            tmp.z += flowVector.z;
        }
        if (blockLiquid != null) {
            final var len = tmp.length();
            final var speed = getLiquidMovementSpeed(blockLiquid) * 0.3f;
            if (len > 0) {
                this.motionX += tmp.x / len * speed;
                this.motionY += tmp.y / len * speed;
                this.motionZ += tmp.z / len * speed;
            }
        }
    }

    protected void addPreviousLiquidMovement() {
        if (previousCurrentMotion != null)
            addTmpMoveMotion(previousCurrentMotion);
    }

    protected void handleFloatingMovement() {
        if (this.hasWaterAt(0)) {
            this.motionY += this.getGravity() * getFloatingForceFactor();
        }
    }

    /**
     * Buoyancy coefficient<br>
     * Example:
     * <pre>
     * if (hasWaterAt(this.getFloatingHeight())) { // The entity floats up after entering the water at a specified height
     *     return 1.3;// Because the buoyancy coefficient > 1, the larger the value, the faster the float
     * }
     * return 0.7; // The entity does not enter the water at the specified height. The entity's buoyancy will resist part of the gravity, but it will not float.
     *             // Because the buoyancy coefficient is less than 1, it is best to add this value to the previous value to equal 2, for example 1.3+0.7=2
     * </pre>
     *
     * @return the floating force factor
     */

    public double getFloatingForceFactor() {
        if (hasWaterAt(this.getFloatingHeight())) {
            return 1.3;
        }
        return 0.7;
    }

    /**
     * Get the height of the entity to float to, 0 is the bottom of the entity {@link Entity#getCurrentHeight()} For the top of the entity<br>
     * Example: <br>When the value is 0, the entity's feet touch the horizontal plane<br>When the value is getCurrentHeight/2, the entity's middle 
     * part touches the horizontal plane<br>When the value is getCurrentHeight, the entity's head touches the horizontal plane
     *
     * @return the float
     */
    public float getFloatingHeight() {
        return this.getEyeHeight();
    }

    protected void handleCollideMovement(int currentTick) {
        var selfAABB = getOffsetBoundingBox().getOffsetBoundingBox(this.motionX, this.motionY, this.motionZ);
        var collidingEntities = this.level.fastCollidingEntities(selfAABB, this);
        collidingEntities.removeIf(entity -> !(entity.canCollide() && (entity instanceof EntityPhysical || entity instanceof Player)));

        var size = collidingEntities.size();
        if (size == 0) {
            this.previousCollideMotion.setX(0);
            this.previousCollideMotion.setZ(0);
            return;
        } else {
            if (!onCollide(currentTick, collidingEntities)) {
                return;
            }
        }

        var dxPositives = new DoubleArrayList(size);
        var dxNegatives = new DoubleArrayList(size);
        var dzPositives = new DoubleArrayList(size);
        var dzNegatives = new DoubleArrayList(size);

        var stream = collidingEntities.stream();
        if (size > 4) {
            stream = stream.parallel();
        }

        stream.forEach(each -> {
            AxisAlignedBB targetAABB;
            if (each instanceof EntityPhysical entityPhysical) {
                targetAABB = entityPhysical.getOffsetBoundingBox();
            } else if (each instanceof Player player) {
                targetAABB = player.reCalcOffsetBoundingBox();
            } else {
                return;
            }

            double centerXWidth = (targetAABB.getMaxX() + targetAABB.getMinX() - selfAABB.getMaxX() - selfAABB.getMinX()) * 0.5;
            double centerZWidth = (targetAABB.getMaxZ() + targetAABB.getMinZ() - selfAABB.getMaxZ() - selfAABB.getMinZ()) * 0.5;

            if (centerXWidth > 0) {
                double value = (targetAABB.getMaxX() - targetAABB.getMinX()) + (selfAABB.getMaxX() - selfAABB.getMinX()) * 0.5 - centerXWidth;
                dxPositives.add(value);
            } else {
                double value = (targetAABB.getMaxX() - targetAABB.getMinX()) + (selfAABB.getMaxX() - selfAABB.getMinX()) * 0.5 + centerXWidth;
                dxNegatives.add(value);
            }

            if (centerZWidth > 0) {
                double value = (targetAABB.getMaxZ() - targetAABB.getMinZ()) + (selfAABB.getMaxZ() - selfAABB.getMinZ()) * 0.5 - centerZWidth;
                dzPositives.add(value);
            } else {
                double value = (targetAABB.getMaxZ() - targetAABB.getMinZ()) + (selfAABB.getMaxZ() - selfAABB.getMinZ()) * 0.5 + centerZWidth;
                dzNegatives.add(value);
            }
        });

        double resultX = (size > 4 ? dxPositives.doubleParallelStream() : dxPositives.doubleStream()).max().orElse(0)
                       - (size > 4 ? dxNegatives.doubleParallelStream() : dxNegatives.doubleStream()).max().orElse(0);
        double resultZ = (size > 4 ? dzPositives.doubleParallelStream() : dzPositives.doubleStream()).max().orElse(0)
                       - (size > 4 ? dzNegatives.doubleParallelStream() : dzNegatives.doubleStream()).max().orElse(0);
        double len = Math.sqrt(resultX * resultX + resultZ * resultZ);

        double finalX = -(resultX / len * 0.2 * 0.32);
        double finalZ = -(resultZ / len * 0.2 * 0.32);

        this.previousCollideMotion.setX(finalX);
        this.previousCollideMotion.setZ(finalZ);
    }


    /**
     * @param collidingEntities Colliding Entities
     * @return false to intercept entity collision motion calculation
     */
    protected boolean onCollide(int currentTick, List<Entity> collidingEntities) {
        if (currentTick % 10 == 0) {
            if (collidingEntities.size() > 24) {
                this.needsCollisionDamage = true;
            }
        }
        return true;
    }

    protected final float getLiquidMovementSpeed(BlockLiquid liquid) {
        if (liquid instanceof BlockFlowingLava) {
            return 0.02f;
        }
        return 0.05f;
    }

    public float getFootHeight() {
        return getCurrentHeight() / 2 - 0.1f;
    }

    protected void calculateOffsetBoundingBox() {
        // Because it is asyncPrepare, this.offsetBoundingBox has a chance to be null, so it needs to be judged as null
        if (this.offsetBoundingBox == null) return;
        final double dx = this.getWidth() * 0.5;
        final double dz = this.getHeight() * 0.5;
        this.offsetBoundingBox.setMinX(this.x - dx);
        this.offsetBoundingBox.setMaxX(this.x + dz);
        this.offsetBoundingBox.setMinY(this.y);
        this.offsetBoundingBox.setMaxY(this.y + this.getHeight());
        this.offsetBoundingBox.setMinZ(this.z - dz);
        this.offsetBoundingBox.setMaxZ(this.z + dz);
    }

    public AxisAlignedBB getOffsetBoundingBox() {
        return Objects.requireNonNullElseGet(this.offsetBoundingBox, () -> new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0));
    }

    public void resetFallDistance() {
        this.fallingTick = 0;
        super.resetFallDistance();
    }

    @Override
    public float getGravity() {
        return super.getGravity();
    }

    public int getFallingTick() {
        return this.fallingTick;
    }
}