package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityIntelligent extends EntityPhysical {

    protected boolean isJumping = false;

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void asyncPrepare(int currentTick) {
        // 处理运动
        handleJumping();
        super.asyncPrepare(currentTick);
    }

    /**
     * 让实体跳跃
     *
     * @return 是否成功开始跳跃，如果实体在空中或因其他原因无法开始跳跃则返回false
     */
    public boolean jump() {
        if (!this.isOnGround()) {
            return false;
        }
        isJumping = true;
        return true;
    }

    public float getJumpingHeight() {
        return 1.25f;
    }

    protected void handleJumping() {
        if (isJumping) {
            this.motionY += getJumpingHeight() * 0.35;
            this.isJumping = false;
        }
    }
}
