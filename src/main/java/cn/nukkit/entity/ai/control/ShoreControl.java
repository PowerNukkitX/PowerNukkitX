package cn.nukkit.entity.ai.control;

import cn.nukkit.entity.EntityIntelligent;
import org.jetbrains.annotations.NotNull;

public class ShoreControl implements Control<Void> {
    private final EntityIntelligent entity;
    private ControlState state = ControlState.NOT_WORKING;
    private int lastShoreTick;

    public ShoreControl(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public Void control(int currentTick, boolean needsRecalcMovement) {
        if (entity.isShoring) {
            entity.isShoring = false;
            var dy = entity.getHeight() * 0.43;
            // TODO: 2022/5/8 这里实体上岸会被错误地疯狂触发，这里加了个trick，但是仍需找出问题所在
            if (currentTick - lastShoreTick > 20 && entity.motionY < dy - entity.getGravity()) {
                entity.motionY += dy;
                state = ControlState.JUST_DONE;
                lastShoreTick = currentTick;
            } else {
                state = ControlState.NOT_WORKING;
            }
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
