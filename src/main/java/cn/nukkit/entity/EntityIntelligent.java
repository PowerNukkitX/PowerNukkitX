package cn.nukkit.entity;

import cn.nukkit.entity.control.Control;
import cn.nukkit.entity.control.JumpControl;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityIntelligent extends EntityPhysical {

    public boolean isJumping = false;

    protected Control jumpControl = null;

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, true);
    }

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt, boolean init) {
        super(chunk, nbt);
        if (init) {
            this.jumpControl = new JumpControl(this);
        }
    }

    @Override
    public void asyncPrepare(int currentTick) {
        // 处理运动
        if(jumpControl != null) jumpControl.control(currentTick);
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

}
