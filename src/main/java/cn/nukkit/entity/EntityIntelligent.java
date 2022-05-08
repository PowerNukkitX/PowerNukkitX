package cn.nukkit.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.control.Control;
import cn.nukkit.entity.control.JumpControl;
import cn.nukkit.entity.control.WalkMoveNearControl;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityIntelligent extends EntityPhysical {

    public boolean isJumping = false;
    public Vector3 movingNearDestination = null;

    public Vector3 previousMoveNearMotion = Vector3.ZERO;

    protected Control<?> jumpControl = null;
    protected Control<? extends Vector3> moveNearControl = null;

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, true);
    }

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt, boolean init) {
        super(chunk, nbt);
        if (init) {
            this.jumpControl = new JumpControl(this);
            this.moveNearControl = new WalkMoveNearControl(this);
        }
    }

    @Override
    public void asyncPrepare(int currentTick) {
        // 处理运动
        super.asyncPrepare(currentTick);
        if (moveNearControl != null) previousMoveNearMotion = moveNearControl.control(currentTick, needsRecalcMovement);
        addTmpMoveMotionXZ(previousMoveNearMotion);
        if (jumpControl != null) jumpControl.control(currentTick, needsRecalcMovement);
    }

    /**
     * 让实体跳跃
     *
     * @return 是否成功开始跳跃，如果实体在空中或因其他原因无法开始跳跃则返回false
     */
    public boolean jump() {
        if (!this.isOnGround() || isJumping) {
            return false;
        }
        isJumping = true;
        return true;
    }

    public float getJumpingHeight() {
        return 1.25f;
    }

    /**
     * 移动至临近的目标点，目标点距离距离应小于1.733，不强制要求
     *
     * @param destination 目标点
     * @return 该目标点是否能够到达
     */
    public boolean moveNear(Vector3 destination) {
        if (level.getCollisionBlocks(new SimpleAxisAlignedBB(destination.x - getWidth() * 0.5, destination.y, destination.z - getWidth() * 0.5,
                        destination.x + getWidth() * 0.5, destination.y + getHeight(), destination.z + getWidth() * 0.5),
                true, false, Block::isSolid).length == 0) {
            movingNearDestination = destination;
            return true;
        }
        return false;
    }

}
