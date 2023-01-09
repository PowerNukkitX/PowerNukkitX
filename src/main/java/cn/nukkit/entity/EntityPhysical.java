package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.*;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntityPhysical extends EntityCreature implements EntityAsyncPrepare {
    /**
     * 移动精度阈值，绝对值小于此阈值的移动被视为没有移动
     */
    public static final float PRECISION = 0.00001f;

    public static final AtomicInteger globalCycleTickSpread = new AtomicInteger();
    /**
     * 时间泛播延迟，用于缓解在同一时间大量提交任务挤占cpu的情况
     */
    public final int tickSpread;
    /**
     * 提供实时最新碰撞箱位置
     */
    protected final AxisAlignedBB offsetBoundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);
    protected final Vector3 previousCollideMotion = new Vector3();
    protected final Vector3 previousCurrentMotion = new Vector3();
    /**
     * 实体自由落体运动的时间
     */
    protected int fallingTick = 0;
    protected boolean needsRecalcMovement = true;
    private boolean needsCollisionDamage = false;

    public EntityPhysical(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.tickSpread = globalCycleTickSpread.getAndIncrement() & 0xf;
    }

    @Override
    public void asyncPrepare(int currentTick) {
        // 计算是否需要重新计算高开销实体运动
        this.needsRecalcMovement = this.level.tickRateOptDelay == 1 || ((currentTick + tickSpread) & (this.level.tickRateOptDelay - 1)) == 0;
        // 重新计算绝对位置碰撞箱
        this.calculateOffsetBoundingBox();
        if (!this.isImmobile()) {
            // 处理重力
            handleGravity();
            if (needsRecalcMovement) {
                // 处理碰撞箱挤压运动
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
        // 记录最大高度，用于计算坠落伤害
        if (!this.onGround && this.y > highestPosition) {
            this.highestPosition = this.y;
        }
        // 添加挤压伤害
        if (needsCollisionDamage) {
            this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.COLLIDE, 3));
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return true;
    }

    @Override
    public void updateMovement() {
        // 检测自由落体时间
        if (isFalling()) {
            this.fallingTick++;
        }
        super.updateMovement();
        this.move(this.motionX, this.motionY, this.motionZ);
    }

    @PowerNukkitXOnly
    @Since("1.19.50-r4")
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
        //重力一直存在
        this.motionY -= this.getGravity();
        if (!this.onGround && this.hasWaterAt(getFootHeight())) {
            //落地水
            resetFallDistance();
        }
    }

    /**
     * 计算地面摩擦力
     */
    @Since("1.19.50-r4")
    protected void handleGroundFrictionMovement() {
        //未在地面就没有地面阻力
        if (!this.onGround) return;
        //小于精度
        if (Math.abs(this.motionZ) < PRECISION && Math.abs(this.motionX) < PRECISION) return;
        // 减少移动向量（计算摩擦系数，在冰上滑得更远）
        final double factor = getGroundFrictionFactor();
        this.motionX *= factor;
        this.motionZ *= factor;
        if (Math.abs(this.motionX) < PRECISION) this.motionX = 0;
        if (Math.abs(this.motionZ) < PRECISION) this.motionZ = 0;
    }

    /**
     * 计算流体阻力（空气/液体）
     */
    @Since("1.19.50-r4")
    protected void handlePassableBlockFrictionMovement() {
        //小于精度
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
     * 计算当前位置的地面摩擦因子
     *
     * @return 当前位置的地面摩擦因子
     */
    @Since("1.19.50-r4")
    public double getGroundFrictionFactor() {
        if (!this.onGround) return 1.0;
        return this.getLevel().getTickCachedBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getFrictionFactor();
    }

    /**
     * 计算当前位置的流体阻力因子（空气/水）
     *
     * @return 当前位置的流体阻力因子
     */
    @Since("1.19.50-r4")
    public double getPassableBlockFrictionFactor() {
        var block =  this.getTickCachedLevelBlock();
        if (block.collidesWithBB(this.getBoundingBox(), true)) return block.getPassableBlockFrictionFactor();
        return Block.DEFAULT_AIR_FLUID_FRICTION;
    }

    /**
     * 默认使用nk内置实现，这只是个后备算法
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
        addTmpMoveMotion(previousCurrentMotion);
    }

    @Since("1.6.0.0-PNX")
    protected boolean hasWaterAt(float height) {
        double y = this.y + height;
        Block block = this.level.getTickCachedBlock(this.temporalVector.setComponents(NukkitMath.floorDouble(this.x), NukkitMath.floorDouble(y), NukkitMath.floorDouble(this.z)));

        boolean layer1 = false;
        if (!(block instanceof BlockBubbleColumn) && (
                block instanceof BlockWater
                        || (layer1 = block.getTickCachedLevelBlockAtLayer(1) instanceof BlockWater))) {
            BlockWater water = (BlockWater) (layer1 ? block.getTickCachedLevelBlockAtLayer(1) : block);
            double f = (block.y + 1) - (water.getFluidHeightPercent() - 0.1111111);
            return y < f;
        }

        return false;
    }

    protected void handleFloatingMovement() {
        this.motionY += this.getGravity() * getFloatingForceFactor();
    }

    /**
     * 浮力系数
     */
    @Since("1.19.50-r4")
    public double getFloatingForceFactor() {
        if (this.hasWaterAt(0)) {
            //浮力应大于重力
            if (this.isInsideOfWater()) {
                return 1.3;
            }
            return 0.9;
        }
        //未在水里
        return 0;
    }

    protected void handleCollideMovement(int currentTick) {
        var selfAABB = getOffsetBoundingBox().getOffsetBoundingBox(this.motionX, this.motionY, this.motionZ);
        var collidingEntities = this.level.fastCollidingEntities(selfAABB, this);
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
            // 计算碰撞箱
            double centerXWidth = (targetAABB.getMaxX() + targetAABB.getMinX() - selfAABB.getMaxX() - selfAABB.getMinX()) * 0.5;
            double centerZWidth = (targetAABB.getMaxZ() + targetAABB.getMinZ() - selfAABB.getMaxZ() - selfAABB.getMinZ()) * 0.5;
            if (centerXWidth > 0) {
                dxPositives.add((targetAABB.getMaxX() - targetAABB.getMinX()) + (selfAABB.getMaxX() - selfAABB.getMinX()) * 0.5 - centerXWidth);
            } else {
                dxNegatives.add((targetAABB.getMaxX() - targetAABB.getMinX()) + (selfAABB.getMaxX() - selfAABB.getMinX()) * 0.5 + centerXWidth);
            }
            if (centerZWidth > 0) {
                dzPositives.add((targetAABB.getMaxZ() - targetAABB.getMinZ()) + (selfAABB.getMaxZ() - selfAABB.getMinZ()) * 0.5 - centerZWidth);
            } else {
                dzNegatives.add((targetAABB.getMaxZ() - targetAABB.getMinZ()) + (selfAABB.getMaxZ() - selfAABB.getMinZ()) * 0.5 + centerZWidth);
            }
        });
        double resultX = (size > 4 ? dxPositives.doubleParallelStream() : dxPositives.doubleStream()).max().orElse(0) - (size > 4 ? dxNegatives.doubleParallelStream() : dxNegatives.doubleStream()).max().orElse(0);
        double resultZ = (size > 4 ? dzPositives.doubleParallelStream() : dzPositives.doubleStream()).max().orElse(0) - (size > 4 ? dzNegatives.doubleParallelStream() : dzNegatives.doubleStream()).max().orElse(0);
        double len = Math.sqrt(resultX * resultX + resultZ * resultZ);
        this.previousCollideMotion.setX(-(resultX / len * 0.2 * 0.32));
        this.previousCollideMotion.setZ(-(resultZ / len * 0.2 * 0.32));
    }

    /**
     * @param collidingEntities 碰撞的实体
     * @return false以拦截实体碰撞运动计算
     */
    protected boolean onCollide(int currentTick, List<Entity> collidingEntities) {
        if (currentTick % 10 == 0) {
            if (collidingEntities.stream().filter(Entity::canCollide).count() > 24) {
                this.needsCollisionDamage = true;
            }
        }
        return true;
    }

    protected final float getLiquidMovementSpeed(BlockLiquid liquid) {
        if (liquid instanceof BlockLava) {
            return 0.02f;
        }
        return 0.05f;
    }

    public float getFootHeight() {
        return getCurrentHeight() / 2 - 0.1f;
    }

    protected void calculateOffsetBoundingBox() {
        final double dx = this.getWidth() * 0.5;
        final double dz = this.getHeight() * 0.5;
        //由于是asyncPrepare,this.offsetBoundingBox有几率为null，需要判空
        if (this.offsetBoundingBox == null) return;
        this.offsetBoundingBox.setMinX(this.x - dx);
        this.offsetBoundingBox.setMaxX(this.x + dz);
        this.offsetBoundingBox.setMinY(this.y);
        this.offsetBoundingBox.setMaxY(this.y + this.getHeight());
        this.offsetBoundingBox.setMinZ(this.z - dz);
        this.offsetBoundingBox.setMaxZ(this.z + dz);
    }

    public AxisAlignedBB getOffsetBoundingBox() {
        return this.offsetBoundingBox;
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
