package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntityPhysical extends EntityCreature {
    /**
     * 移动精度阈值，绝对值小于此阈值的移动被视为没有移动
     */
    public static final float PRECISION = 0.00001f;
    /**
     * 实体自由落体运动的时间
     */
    protected int fallingTick = 0;
    /**
     * 提供实时最新碰撞箱位置
     */
    protected final AxisAlignedBB offsetBoundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);

    public EntityPhysical(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        // 记录最大高度，用于计算坠落伤害
        if (!this.onGround && this.y > highestPosition) {
            this.highestPosition = this.y;
        }
        // 重新计算绝对位置碰撞箱
        this.calculateOffsetBoundingBox();
        // 处理运动
        handleGravity();
        handleFrictionMovement();
        return super.onUpdate(currentTick);
    }

    @Override
    public void updateMovement() {
        // 检测自由落体时间
        if (!this.onGround && this.y < this.highestPosition) {
            this.fallingTick++;
        }
        super.updateMovement();
        this.move(this.motionX, this.motionY, this.motionZ);
    }

    protected void handleGravity() {
        if (!this.onGround) {
            final int fallingTick = getFallingTick();
            if (fallingTick == 0) {
                this.motionY -= this.getGravity();
            } else {
                this.motionY += (StrictMath.pow(this.getGravity() * 12.25, fallingTick * 0.5) - 1) * 3.92;
            }
        }
    }

    protected void handleFrictionMovement() {
        // 减少移动向量（计算摩擦系数，在冰上滑得更远）
        final double friction = this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getFrictionFactor();
        final double reduce = getMovementSpeed() * (1 - friction * 0.85) * 0.43;
        if (Math.abs(this.motionZ) < PRECISION && Math.abs(this.motionX) < PRECISION) {
            return;
        }
        final double angle = StrictMath.atan2(this.motionZ, this.motionX);
        double tmp = (StrictMath.cos(angle) * reduce);
        if (this.motionX > PRECISION) {
            this.motionX -= tmp;
            if (this.motionX < PRECISION) {
                this.motionX = 0;
            }
        } else if (this.motionX < -PRECISION) {
            this.motionX -= tmp;
            if (this.motionX > -PRECISION) {
                this.motionX = 0;
            }
        } else {
            this.motionX = 0;
        }
        tmp = (StrictMath.sin(angle) * reduce);
        if (this.motionZ > PRECISION) {
            this.motionZ -= tmp;
            if (this.motionZ < PRECISION) {
                this.motionZ = 0;
            }
        } else if (this.motionZ < -PRECISION) {
            this.motionZ -= tmp;
            if (this.motionZ > -PRECISION) {
                this.motionZ = 0;
            }
        } else {
            this.motionZ = 0;
        }
    }

    protected void calculateOffsetBoundingBox() {
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
        return this.offsetBoundingBox;
    }

    public void resetFallDistance() {
        this.fallingTick = 0;
        super.resetFallDistance();
    }

    public int getFallingTick() {
        return this.fallingTick;
    }
}
