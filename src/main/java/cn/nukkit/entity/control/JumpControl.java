package cn.nukkit.entity.control;

import cn.nukkit.entity.EntityIntelligent;

public class JumpControl implements Control<Void> {
    private final EntityIntelligent entity;

    public JumpControl(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public Void control(int currentTick) {
        if (entity.isJumping) {
            entity.motionY += entity.getJumpingHeight() * 0.43;
            entity.isJumping = false;
        }
        return null;
    }
}
