package cn.nukkit.entity;

import cn.nukkit.entity.control.Control;
import cn.nukkit.entity.control.JumpControl;
import cn.nukkit.entity.control.WalkMoveNearControl;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityIntelligent extends EntityPhysical {

    public boolean isJumping = false;
    public boolean doingJumping = false;
    public Vector3 movingNearDestination = null;

    protected Control jumpControl = null;
    protected Control moveNearControl = null;

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
        if (doingJumping && this.isOnGround()) {
            doingJumping = false;
        }
        if (moveNearControl != null) moveNearControl.control(currentTick);
        if (jumpControl != null) jumpControl.control(currentTick);
    }

    /**
     * 让实体跳跃
     *
     * @return 是否成功开始跳跃，如果实体在空中或因其他原因无法开始跳跃则返回false
     */
    public boolean jump() {
        if (!this.isOnGround() || isJumping || doingJumping) {
            return false;
        }
        isJumping = true;
        doingJumping = true;
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
        if (level.getCollisionBlocks(this.offsetBoundingBox, true, false, block -> !block.isSolid()).length == 0) {
            movingNearDestination = destination;
            return true;
        }
        return false;
    }

}
