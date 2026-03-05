package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlowingLava;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.ai.controller.EntityControlUtils;
import cn.nukkit.entity.components.DashActionComponent;
import cn.nukkit.entity.components.RideableComponent;
import cn.nukkit.entity.custom.CustomEntityComponents;
import cn.nukkit.entity.custom.CustomEntityDefinition;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.EntityFreezeEvent;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector2f;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.PlayerAuthInputPacket;
import cn.nukkit.network.protocol.types.AuthInputAction;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public abstract class EntityPhysical extends EntityCreature implements EntityAsyncPrepare {
    /** Movement accuracy threshold. Movements with an absolute value less than this threshold are considered as no movement. */
    public static final float PRECISION = 0.00001f;
    public static final AtomicInteger globalCycleTickSpread = new AtomicInteger();
    /** Time flooding delay is used to alleviate the situation where a large number of tasks are submitted at the same time and occupy the CPU. */
    public final int tickSpread;
    /** Provide real-time latest collision box position */
    protected final AxisAlignedBB offsetBoundingBox;
    protected final Vector3 previousCollideMotion;
    protected final Vector3 previousCurrentMotion;
    /** The time of free fall of an object */
    protected int fallingTick = 0;
    protected boolean needsRecalcMovement = true;
    private boolean needsCollisionDamage = false;
    private static final double GROUND_FRICTION_EXPONENT = 0.5574929506502402;

    protected int rideJumpingTicks = -1;
    protected final AtomicInteger rideJumping = new AtomicInteger(-1);
    protected int rideSprintingTicks = 0;
    private int powerDashingTicks = -1;
    private int dashCooldownEndTick = -1;
    private boolean waterDashChargeStartedInWater = false;


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

        // Dash counter
        if (this.hasDashCooldown()) updateDashAnimationFlag();

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
                //this.setMovementSpeed(); // TODO: Add freeze deceleration to physics entities
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
        if (this.hasGravity() && isFalling()) {
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
        if (!this.hasGravity()) {
            resetFallDistance();
            this.fallingTick = 0;
            return;
        }
        // Gravity is always there
        this.motionY -= this.getGravity();
        if (!this.onGround && this.hasWaterAt(getFootHeight())) {
            // Landing water
            resetFallDistance();
        }
    }

    /** Calculating ground friction */
    protected void handleGroundFrictionMovement() {
        // No ground resistance
        if (!this.onGround) return;
        // Less than precision
        if (Math.abs(this.motionZ) < PRECISION && Math.abs(this.motionX) < PRECISION) return;

        // Reduce movement vector (calculate friction coefficient, slide further on ice)
        double factor = getGroundFrictionFactor();
        if (factor > 0.0 && factor < 1.0) {
            factor = Math.pow(factor, GROUND_FRICTION_EXPONENT);
        }

        this.motionX *= factor;
        this.motionZ *= factor;

        if (Math.abs(this.motionX) < PRECISION) this.motionX = 0;
        if (Math.abs(this.motionZ) < PRECISION) this.motionZ = 0;
    }

    /** Calculate fluid resistance (air/liquid) */
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
     * @return The ground friction factor at the current location
     */
    public double getGroundFrictionFactor() {
        if (!this.onGround) return 1.0;
        return this.getLevel().getTickCachedBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z))).getFrictionFactor();
    }

    /**
     * Calculate the fluid resistance factor (air/water) at the current location
     * @return The fluid resistance factor at the current location
     */
    public double getPassableBlockFrictionFactor() {
        var block = this.getTickCachedLevelBlock();
        if (block.collidesWithBB(this.getBoundingBox(), true)) return block.getPassableBlockFrictionFactor();
        return Block.DEFAULT_AIR_FLUID_FRICTION;
    }

    /** By default, the built-in implementation of nk is used, which is just a fallback algorithm. */
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
     * @return the float
     */
    public float getFloatingHeight() {
        return this.getEyeHeight();
    }

    protected void handleCollideMovement(int currentTick) {
        if (!this.canBePushedByEntities()) {
            this.previousCollideMotion.setX(0);
            this.previousCollideMotion.setZ(0);
            return;
        }

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
                this.previousCollideMotion.setX(0);
                this.previousCollideMotion.setZ(0);
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
        if (this.offsetBoundingBox == null) return;
        final double half = this.getWidth() * 0.5;
        this.offsetBoundingBox.setMinX(this.x - half);
        this.offsetBoundingBox.setMaxX(this.x + half);
        this.offsetBoundingBox.setMinY(this.y);
        this.offsetBoundingBox.setMaxY(this.y + this.getHeight());
        this.offsetBoundingBox.setMinZ(this.z - half);
        this.offsetBoundingBox.setMaxZ(this.z + half);
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
        return this.hasGravity() ? super.getGravity() : 0f;
    }

    public int getFallingTick() {
        return this.fallingTick;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (this.isRideable()) {
            if (entity == null) return false;
            if (this.passengers != null && this.isPassenger(entity)) return false;
        }

        return super.canCollideWith(entity);
    }

    @Override
    public boolean onRiderInput(Player rider, PlayerAuthInputPacket pk) {
        if (rider.isAnyUiOpen()) return false;
        RideableComponent.InputType type = getInputControlType();
        if (type == RideableComponent.InputType.GROUND || type == RideableComponent.InputType.WATER) {
            if (handleRideJumpOrDash(pk, type)) return true;
        }

        return switch (type) {
            case GROUND -> onRiderInputGroundControlled(rider, pk);
            case AIR    -> onRiderInputAirControlled(rider, pk);
            case WATER  -> onRiderInputWaterControlled(rider, pk);
            default     -> false;
        };
    }

    /** Ground Input Controls */
    public boolean onRiderInputGroundControlled(Player rider, PlayerAuthInputPacket pk) {
        int controlSeat = getControllingSeatIndex();
        if (controlSeat < 0 || controlSeat >= passengers.size() || passengers.get(controlSeat) != rider) return false;

        if (!(this instanceof EntityControlUtils me)) return false;
        me.setMoveTarget(null);
        me.setLookTarget(null);

        // INPUT KNOBS
        final double DEADZONE = 0.08;        // stick drift tolerance
        final double CURVE_EXP = 1.6;        // >1 = more precision at low input
        final double ACCEL_PER_TICK = 0.30;  // how fast it ramps up (0..1)
        final double BRAKE_PER_TICK = 0.65;  // how hard it brakes (0..1)
        final double SPEED_KNOB = 1.80d;     // Knob to parity with BDS speed

        // AIR / GROUND
        if (isOnGround() || level.getTick() - getRideJumping().get() <= 5) {
        Vector2 raw = pk.motion;
        final double GROUND_BACKWARDS_MOVEMENT_MODIFIER = 0.5d;
        double inX = raw.x;
        double inY = raw.y;
        if (inY < 0.0d) {
            inY *= GROUND_BACKWARDS_MOVEMENT_MODIFIER;
        }
        Vector2 adjusted = new Vector2(inX, inY);
        double mag = adjusted.length();

            double curX = this.motionX;
            double curZ = this.motionZ;

            if (mag <= DEADZONE) {
                double brake = BRAKE_PER_TICK;
                double newX = curX * (1.0 - brake);
                double newZ = curZ * (1.0 - brake);

                if (newX * newX + newZ * newZ < 0.0004) {
                    newX = 0;
                    newZ = 0;
                }

                this.addTmpMoveMotion(new Vector3(newX - curX, 0, newZ - curZ));

            } else {
            Vector2 dir = adjusted.normalize();

                double yawRad = Math.toRadians(pk.yaw);
                double cos = Math.cos(yawRad);
                double sin = Math.sin(yawRad);

                double wishX = dir.x * cos - dir.y * sin;
                double wishZ = dir.x * sin + dir.y * cos;

                double strength = mag;
                if (strength > 1.0) strength = 1.0;
                strength = (strength - DEADZONE) / (1.0 - DEADZONE);
                if (strength < 0.0) strength = 0.0;
                strength = Math.pow(strength, CURVE_EXP);

                double maxSpeed = this.getDefaultSpeed();
                if (pk.inputData.contains(AuthInputAction.SPRINTING)) {
                    maxSpeed *= this.getSpeedMultiplier();
                    rideSprintingTicks++;
                } else {
                    rideSprintingTicks = 0;
                }

                double friction = SPEED_KNOB;
                int bx = (int) Math.floor(this.x);
                int by = (int) Math.floor(this.y - 0.01);
                int bz = (int) Math.floor(this.z);

                Block under = this.level.getBlock(bx, by, bz);
                if (under != null) {
                    double blockFriction = under.getFrictionFactor();
                    if (Double.isFinite(blockFriction) && blockFriction > 0.0) {
                        friction *= blockFriction;
                    }
                }
                if (!Double.isFinite(friction) || friction < 0.001d) friction = 0.001d;

                double targetX = (wishX * maxSpeed * strength) / friction;
                double targetZ = (wishZ * maxSpeed * strength) / friction;

                double accel = ACCEL_PER_TICK;
                double newX = curX + (targetX - curX) * accel;
                double newZ = curZ + (targetZ - curZ) * accel;

                double cap = (maxSpeed / friction);
                double s2 = newX * newX + newZ * newZ;
                double m2 = cap * cap;
                if (s2 > m2) {
                    double inv = cap / Math.sqrt(s2);
                    newX *= inv;
                    newZ *= inv;
                }

                this.addTmpMoveMotion(new Vector3((newX - curX), 0.0d, (newZ - curZ)));
            }

        } else {
            if (!isRideJumping() || level.getTick() - getRideJumping().get() > 5) {
                handleGravity();
                handleFloatingMovement();
            }
        }

        this.yaw = pk.yaw;
        this.headYaw = pk.yaw;
        return true;
    }

    /** Air Input Controls */
    public boolean onRiderInputAirControlled(Player rider, PlayerAuthInputPacket pk) {
        int controlSeat = getControllingSeatIndex();
        if (controlSeat < 0 || controlSeat >= passengers.size() || passengers.get(controlSeat) != rider) return false;

        if (!(this instanceof EntityControlUtils me)) return false;
        me.setMoveTarget(null);
        me.setLookTarget(null);

        // INPUT KNOBS
        final double HORIZONTAL_TUNE = 0.97d;   // Horizontal movement speed
        final double VERTICAL_TUNE   = 0.60d;   // Vertical movement speed
        final double FRICTION_KNOB = 7.0d;      // Knob to parity with BDS speed

        setYaw(pk.interactRotation.y);
        setHeadYaw(pk.interactRotation.y);
        setPitch(pk.interactRotation.x);

        Vector2f input = pk.rawMoveVector;
        float forward = input.y;
        float strafe = input.x;

        float strafeSpeedModifier = this.getAirStrafeSpeedModifier();
        float backwardsMovementModifier = this.getAirBackwardsMovementModifier();

        strafe *= strafeSpeedModifier;
        if (forward < 0f) forward *= backwardsMovementModifier;

        boolean rushing = pk.inputData.contains(AuthInputAction.SPRINT_DOWN)
                || pk.inputData.contains(AuthInputAction.SPRINTING)
                || pk.inputData.contains(AuthInputAction.START_SPRINTING);

        boolean upPressed = pk.inputData.contains(AuthInputAction.WANT_UP)
                || pk.inputData.contains(AuthInputAction.JUMP_DOWN)
                || pk.inputData.contains(AuthInputAction.JUMPING)
                || pk.inputData.contains(AuthInputAction.START_JUMPING);

        double speed = this.getDefaultFlyingSpeed() * FRICTION_KNOB;
        if (rushing) speed *= this.getSpeedMultiplier();

        if (Math.abs(forward) < 0.01f && Math.abs(strafe) < 0.01f && !upPressed) {
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;

            updateMovement();
            return true;
        }

        double yawRad = Math.toRadians(this.yaw);
        double dx = (-Math.sin(yawRad) * forward + Math.cos(yawRad) * strafe) * speed * HORIZONTAL_TUNE;
        double dz = ( Math.cos(yawRad) * forward + Math.sin(yawRad) * strafe) * speed * HORIZONTAL_TUNE;
        double pitch = Math.max(-80, Math.min(80, pk.interactRotation.x));
        double pitchRad = Math.toRadians(pitch);
        double dy = -Math.sin(pitchRad) * speed * VERTICAL_TUNE;
        if (upPressed) dy = speed * VERTICAL_TUNE;

        motionX = dx;
        motionY = dy;
        motionZ = dz;

        moveFlying(forward, strafe, 0);
        updateMovement();
        return true;
    }

    /** Water Input Controls */
    public boolean onRiderInputWaterControlled(Player rider, PlayerAuthInputPacket pk) {
        int controlSeat = getControllingSeatIndex();
        if (controlSeat < 0 || controlSeat >= passengers.size() || passengers.get(controlSeat) != rider) return false;

        if (!(this instanceof EntityControlUtils me)) return false;
        me.setMoveTarget(null);
        me.setLookTarget(null);

        // INPUT KNOBS
        final double HORIZONTAL_TUNE = 0.97d;
        final double VERTICAL_TUNE   = 0.60d;
        final double SPEED_KNOB = 2.40d; // Knob to parity with BDS speed
        final double WATER_DASH_DRAG = 0.88d;
        final double OUT_OF_WATER_EXTRA_GRAVITY = 0.05d;
        final double MAX_UPWARD_WHILE_OUT = 0.35d;

        setYaw(pk.interactRotation.y);
        setHeadYaw(pk.interactRotation.y);
        setPitch(pk.interactRotation.x);

        // Water surface detection on our X/Z column.
        final int bx = (int) Math.floor(this.x);
        final int bz = (int) Math.floor(this.z);

        int yMin = (int) Math.floor(this.y) - 2;
        int yMax = (int) Math.floor(this.y + this.getHeight()) + 2;

        int topWaterBlockY = Integer.MIN_VALUE;
        for (int y = yMax; y >= yMin; y--) {
            Block b = this.level.getBlock(bx, y, bz);
            if (b instanceof BlockWater || b instanceof BlockFlowingWater) {
                topWaterBlockY = y;
                break;
            }
        }

        double surfaceY = (topWaterBlockY != Integer.MIN_VALUE) ? (topWaterBlockY + 1.0d) : Double.NaN;

        boolean inWaterColumn = false;
        if (this.isTouchingWater()) {
            inWaterColumn = true;
        } else if (topWaterBlockY != Integer.MIN_VALUE) {
            double sY = topWaterBlockY + 1.0d;
            final double BREACH_EPS = 0.25d;
            inWaterColumn = (this.y <= sY + BREACH_EPS);
        }

        // Keep at least 65% of body submerged while moving (max 35% outside water)
        double maxFeetY = Double.NaN;
        if (Double.isFinite(surfaceY)) {
            maxFeetY = surfaceY - (this.getHeight() * 0.65d);
        }

        // Movement input
        Vector2f input = pk.rawMoveVector;
        float forward = input.y;
        float strafe  = input.x;

        float strafeSpeedModifier = getAirStrafeSpeedModifier();
        float backwardsMovementModifier = getAirBackwardsMovementModifier();
        strafe *= strafeSpeedModifier;
        if (forward < 0f) forward *= backwardsMovementModifier;

        boolean rushing =
                pk.inputData.contains(AuthInputAction.SPRINT_DOWN) ||
                pk.inputData.contains(AuthInputAction.SPRINTING) ||
                pk.inputData.contains(AuthInputAction.START_SPRINTING);

        double speed = getDefaultUnderWaterSpeed() * SPEED_KNOB;
        if (rushing) speed *= getSpeedMultiplier();

        boolean moving = (Math.abs(forward) >= 0.01f) || (Math.abs(strafe) >= 0.01f);
        final double DASH_EPS2 = 0.02d * 0.02d;
        boolean isDashing =
                (powerDashingTicks != -1) ||
                (this.hasDashCooldown() && (this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) > DASH_EPS2);

        if (!moving && inWaterColumn && !isDashing) {
            motionX = 0;
            motionY = 0;
            motionZ = 0;
            updateMovement();
            return true;
        }

        if (!moving && isDashing) {
            if (inWaterColumn) {
                this.motionX *= WATER_DASH_DRAG;
                this.motionZ *= WATER_DASH_DRAG;
            }
            if (!inWaterColumn) {
                if (this.motionY > MAX_UPWARD_WHILE_OUT) this.motionY = MAX_UPWARD_WHILE_OUT;
                this.motionY -= OUT_OF_WATER_EXTRA_GRAVITY;
            }

            updateMovement();
            return true;
        }

        double yawRad = Math.toRadians(this.yaw);
        double desiredX = (-Math.sin(yawRad) * forward + Math.cos(yawRad) * strafe) * speed * HORIZONTAL_TUNE;
        double desiredZ = ( Math.cos(yawRad) * forward + Math.sin(yawRad) * strafe) * speed * HORIZONTAL_TUNE;
        double pitch = Math.max(-80, Math.min(80, pk.interactRotation.x));
        double desiredY = -Math.sin(Math.toRadians(pitch)) * speed * VERTICAL_TUNE;
        final double DRIVE_GRAVITY_BIAS_IN_WATER = 0.01d;
        desiredY -= DRIVE_GRAVITY_BIAS_IN_WATER;

        // Prevent flying out of water during NORMAL swim movement
        if (inWaterColumn && Double.isFinite(maxFeetY)) {
            if (this.y >= maxFeetY) {
                if (desiredY > 0) desiredY = 0;
                if (this.motionY > 0 && !isDashing) this.motionY = 0;
            } else if (desiredY > 0) {
                double headroom = maxFeetY - this.y;
                if (headroom <= 0) desiredY = 0;
                else if (desiredY > headroom) desiredY = headroom;
            }
        } else if (!inWaterColumn) {
            if (desiredY > 0) desiredY = 0;
        }

        // Gravity feel AFTER breaching water
        if (getInputControlType() == RideableComponent.InputType.WATER && !inWaterColumn) {
            if (this.motionY > MAX_UPWARD_WHILE_OUT) this.motionY = MAX_UPWARD_WHILE_OUT;
            if (isDashing) {
                this.motionY -= OUT_OF_WATER_EXTRA_GRAVITY;
            } else {
                desiredY -= OUT_OF_WATER_EXTRA_GRAVITY;
            }
        }

        if (inWaterColumn) {
            final double MAX_DOWNWARD_IN_WATER = -0.10d;
            if (this.motionY < MAX_DOWNWARD_IN_WATER) this.motionY = MAX_DOWNWARD_IN_WATER;
        }

        double curX = this.motionX;
        double curY = this.motionY;
        double curZ = this.motionZ;

        if (isDashing) {
            // Blend input with dash momentum
            final double DASH_INPUT_BLEND = 0.35d; // 0 = pure dash momentum, 1 = pure input
            desiredX = curX + (desiredX - curX) * DASH_INPUT_BLEND;
            desiredZ = curZ + (desiredZ - curZ) * DASH_INPUT_BLEND;
            desiredY = curY;
        }

        this.addTmpMoveMotion(new Vector3(desiredX - curX, desiredY - curY, desiredZ - curZ));
        updateMovement();
        return true;
    }


    // INPUT CONTROLS HELPERS START

    private boolean handleRideJumpOrDash(PlayerAuthInputPacket pk, RideableComponent.InputType type) {
        final boolean dashHeld =
                pk.inputData.contains(AuthInputAction.WANT_UP) ||
                pk.inputData.contains(AuthInputAction.JUMP_DOWN) ||
                pk.inputData.contains(AuthInputAction.JUMPING) ||
                pk.inputData.contains(AuthInputAction.START_JUMPING);

        // POWER JUMP
        if (this.canPowerJump()) {
            final boolean canChargeJump = isOnGround();
            if (dashHeld) {
                if (canChargeJump) {
                    rideJumpingTicks++;
                    return true;
                }
                return false;
            }

            if (rideJumpingTicks != -1) {
                if (isOnGround()) {
                    float charge = Math.min(rideJumpingTicks / 10f, 1.0f);
                    float js = this.getRideJumpStrength();
                    float t = (js - 0.4f) / 0.6f;
                    t = Math.max(0f, Math.min(1f, t));
                    double desiredHeight = 1.0d + (5.5d - 1.0d) * t;
                    double baseMotionY = solveJumpMotionYForHeight(desiredHeight);
                    double motion = baseMotionY * charge;

                    this.getRideJumping().set(this.getLevel().getTick());
                    this.motionY = 0;
                    this.addTmpMoveMotion(new Vector3(0, motion, 0));
                    this.setDataFlag(EntityFlag.STANDING, true);

                    rideJumpingTicks = -1;
                    return true;
                }
                rideJumpingTicks = -1;
                return false;
            }

            return false;
        }

        // DASH
        if (this.canDash()) {
            if (dashHeld) {
                if (powerDashingTicks == -1) {
                    powerDashingTicks = 0;
                    if (type == RideableComponent.InputType.WATER) waterDashChargeStartedInWater = isInWaterForDash();
                }
                powerDashingTicks++;
                return true;
            }

            if (powerDashingTicks != -1) {
                float charge = Math.min(powerDashingTicks / 10f, 1.0f);
                powerDashingTicks = -1;

                if (type == RideableComponent.InputType.WATER) {
                    final DashActionComponent dash = this.getDashAction();
                    if (dash == null) {
                        waterDashChargeStartedInWater = false;
                        return true;
                    }
                    boolean allow = waterDashChargeStartedInWater || isInWaterForDash();
                    waterDashChargeStartedInWater = false;
                    if (!allow) return true;
                    if (!dash.resolvedCanDashUnderwater() && this.isTouchingWater()) return true;
                }

                return tryDash(pk, charge);
            }
        }

        return false;
    }


    protected boolean tryDash(PlayerAuthInputPacket pk, float charge) {
        if (this.isDashOnCooldown()) return false;

        final DashActionComponent dash = this.getDashAction();
        if (dash == null) return false;

        if (this.isTouchingWater() && !dash.resolvedCanDashUnderwater()) return false;

        final float  MIN_CHARGE = 0.05f;
        final float  CHARGE_EXP = 1.40f;
        final double CURVE_EXP  = 1.10d;
        final double H_SCALE    = 0.026d;
        final double V_SCALE    = 0.61355d;
        final double WATER_DASH_HORIZONTAL_SCALE = 0.40d;
        final double WATER_DASH_VERTICAL_SCALE = 0.08d;

        final DashActionComponent.Direction dirMode = dash.resolvedDirection();
        final double yaw   = (dirMode == DashActionComponent.Direction.ENTITY) ? this.yaw : pk.interactRotation.y;
        final float  pitch = (dirMode == DashActionComponent.Direction.PASSENGER) ? pk.interactRotation.x : 0.0f;

        final double yawRad   = Math.toRadians(yaw);
        final double pitchRad = Math.toRadians(pitch);

        double x, y, z;
        if (dirMode == DashActionComponent.Direction.ENTITY) {
            x = -Math.sin(yawRad);
            y = 0.0d;
            z =  Math.cos(yawRad);
        } else {
            final double cosPitch = Math.cos(pitchRad);
            x = -Math.sin(yawRad) * cosPitch;
            y = -Math.sin(pitchRad);
            z =  Math.cos(yawRad) * cosPitch;
        }

        final double len = Math.sqrt(x * x + y * y + z * z);
        if (len < 1.0e-9) return false;
        x /= len; y /= len; z /= len;

        if (charge < 0f) charge = 0f;
        if (charge > 1f) charge = 1f;
        if (charge < MIN_CHARGE) return false;

        final double c  = Math.pow(charge, CHARGE_EXP);
        final double hc = Math.pow(c, CURVE_EXP);
        final double hMomentum = dash.resolvedHorizontalMomentum();
        final double vMomentum = dash.resolvedVerticalMomentum();

        double hImpulse = (hMomentum * c) * H_SCALE * hc;
        final double maxHImpulse = hMomentum * H_SCALE;
        if (hImpulse > maxHImpulse) hImpulse = maxHImpulse;

        double my;
        if (dirMode == DashActionComponent.Direction.ENTITY) {
            my = (vMomentum * V_SCALE) * c;
        } else {
            final double vImpulse = (vMomentum * V_SCALE) * c;
            my = (y * hImpulse) + vImpulse;
        }

        if (getInputControlType() == RideableComponent.InputType.WATER && isInWaterForDash()) {
            hImpulse *= WATER_DASH_HORIZONTAL_SCALE;
            my *= WATER_DASH_VERTICAL_SCALE;
        }

        this.addTmpMoveMotion(new Vector3(x * hImpulse, my, z * hImpulse));
        startDashCooldown(dash.resolvedCooldownTime());
        return true;
    }

    protected boolean isDashOnCooldown() {
        return dashCooldownEndTick != -1 && this.level.getTick() < dashCooldownEndTick;
    }

    protected void startDashCooldown(float seconds) {
        if (seconds < 0f) seconds = 0f;
        int ticks = (int) Math.ceil(seconds * 20.0d);
        if (ticks <= 0) {
            dashCooldownEndTick = -1;
            this.setDashCooldown(false);
            return;
        }

        dashCooldownEndTick = this.level.getTick() + ticks;
        this.setDashCooldown(true);
    }

    protected void updateDashAnimationFlag() {
        if (!this.hasDashCooldown()) return;

        if (!isDashOnCooldown()) {
            this.setDashCooldown(false);
        }
    }

    private boolean isInWaterForDash() {
        if (this.isTouchingWater()) return true;

        final int bx = (int) Math.floor(this.x);
        final int bz = (int) Math.floor(this.z);

        int yMin = (int) Math.floor(this.y) - 2;
        int yMax = (int) Math.floor(this.y + this.getHeight()) + 2;

        for (int y = yMax; y >= yMin; y--) {
            Block b = this.level.getBlock(bx, y, bz);
            if (b instanceof BlockWater || b instanceof BlockFlowingWater) {
                return true;
            }
        }
        return false;
    }

    public float getAirStrafeSpeedModifier() {
        if (!isCustomEntity()) return 0.4f;
        CustomEntityDefinition.Meta.InputAirControlled air = meta().getInputAirControlled(CustomEntityComponents.INPUT_AIR_CONTROLLED);

        if (air == null) return 0.4f;
        return air.strafeSpeedModifier();
    }

    public float getAirBackwardsMovementModifier() {
        if (!isCustomEntity()) return 0.5f;
        CustomEntityDefinition.Meta.InputAirControlled air = meta().getInputAirControlled(CustomEntityComponents.INPUT_AIR_CONTROLLED);

        if (air == null) return 0.5f;
        return air.backwardsMovementModifier();
    }

    public AtomicInteger getRideJumping() {
        return rideJumping;
    }

    public boolean isRideJumping() {
        return this.rideJumping.get() != -1;
    }

    public boolean isRideSprinting() {
        return this.rideSprintingTicks > 0;
    }

    protected double solveJumpMotionYForHeight(double targetHeight) {
        double lo = 0.05d;
        double hi = 2.00d;

        while (simulateJumpHeight(hi) < targetHeight && hi < 10.0d) {
            hi *= 1.5d;
        }

        for (int i = 0; i < 30; i++) {
            double mid = (lo + hi) * 0.5d;
            double h = simulateJumpHeight(mid);
            if (h >= targetHeight) {
                hi = mid;
            } else {
                lo = mid;
            }
        }
        return hi;
    }

    protected double simulateJumpHeight(double motionY) {
        final double DRAG = 0.98d;
        double y = 0.0d;
        double maxY = 0.0d;

        for (int tick = 0; tick < 60; tick++) {
            y += motionY;
            if (y > maxY) maxY = y;
            motionY -= this.getGravity();
            motionY *= DRAG;
            if (motionY < 0 && y < maxY - 0.01d) break;
        }

        return maxY;
    }

    // INPUT CONTROLS HELPERS END
}
