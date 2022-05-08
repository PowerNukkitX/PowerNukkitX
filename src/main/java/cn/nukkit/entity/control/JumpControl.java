package cn.nukkit.entity.control;

import cn.nukkit.entity.EntityIntelligent;
import org.jetbrains.annotations.NotNull;

public class JumpControl implements Control<Void> {
    private final EntityIntelligent entity;
    private ControlState state = ControlState.NOT_WORKING;

    public JumpControl(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public Void control(int currentTick, boolean needsRecalcMovement) {
        if (entity.isJumping) {
            entity.motionY += entity.getJumpingHeight() * 0.43;
            entity.isJumping = false;
            state = ControlState.JUST_DONE;
        } else {
            state = ControlState.NOT_WORKING;
        }
        return null;
    }

    @NotNull
    @Override
    public ControlState getState() {
        return state;
    }
}
