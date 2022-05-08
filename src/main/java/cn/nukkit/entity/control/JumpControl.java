package cn.nukkit.entity.control;

import cn.nukkit.entity.EntityIntelligent;

public class JumpControl implements Control {
    private final EntityIntelligent entity;

    public JumpControl(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public void control(int currentTick) {
        if (entity.isJumping) {
            entity.motionY += entity.getJumpingHeight() * 0.43;
            entity.isJumping = false;
        }
    }
}
