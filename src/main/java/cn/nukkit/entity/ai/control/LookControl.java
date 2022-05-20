package cn.nukkit.entity.ai.control;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.goal.FollowPathGoal;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class LookControl implements Control<Void> {
    private final EntityIntelligent entity;
    private ControlState state = ControlState.NOT_WORKING;

    public LookControl(EntityIntelligent entity) {
        this.entity = entity;
    }

    @Override
    public Void control(int currentTick, boolean needsRecalcMovement) {
        if (needsRecalcMovement) {
            var followPathGoal = entity.getGoal(FollowPathGoal.ID);
            if (entity.lookAtTarget != null) {
                headLookAt(entity.lookAtTarget);
            }
            if (followPathGoal != null && followPathGoal.isRunning() && entity.movingNearDestination != null) {
                bodyLookAt(entity.movingNearDestination);
                return withState(ControlState.JUST_DONE);
            }
        }
        return withState(ControlState.NOT_WORKING);
    }

    private Void withState(ControlState state) {
        this.state = state;
        return null;
    }

    private void bodyLookAt(Vector3 target) {
        double xDiff = target.x - entity.x;
        double zDiff = target.z - entity.z;
        double angle = Math.atan2(zDiff, xDiff);
        entity.yaw = ((angle * 180) / Math.PI) - 90;
        entity.headYaw = entity.yaw;
        entity.pitch = 0;
    }

    private void headLookAt(Vector3 target) {
        double xDiff = target.x - entity.x;
        double zDiff = target.z - entity.z;
        double angle = Math.atan2(zDiff, xDiff);
        double yaw = ((angle * 180) / Math.PI) - 90;
        double yDiff = target.y - entity.y;
        var v = new Vector2(entity.x, entity.z);
        double dist = v.distance(target.x, target.z);
        angle = Math.atan2(dist, yDiff);
        double pitch = ((angle * 180) / Math.PI) - 90;
        entity.headYaw = yaw;
        entity.pitch = pitch;
    }

    @NotNull
    @Override
    public ControlState getState() {
        return state;
    }
}
