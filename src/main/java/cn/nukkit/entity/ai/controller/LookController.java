package cn.nukkit.entity.ai.controller;

import cn.nukkit.entity.EntityIntelligent;
import java.util.function.BooleanSupplier;
import cn.nukkit.math.BVector3;
import cn.nukkit.math.Vector3;

/**
 * Handle entity Pitch/Yaw/HeadYaw rotation to look at targets or move direction.
 */
public class LookController implements IController {

    protected final BooleanSupplier lookAtTarget;
    protected final BooleanSupplier lookAtRoute;

    public LookController(boolean lookAtTarget, boolean lookAtRoute) {
        this.lookAtTarget = () -> lookAtTarget;
        this.lookAtRoute = () -> lookAtRoute;
    }

    public LookController(BooleanSupplier lookAtTarget, BooleanSupplier lookAtRoute) {
        this.lookAtTarget = lookAtTarget;
        this.lookAtRoute = lookAtRoute;
    }

    @Override
    public boolean control(EntityIntelligent entity) {
        Vector3 lookTarget = entity.getLookTarget();

        if (lookAtRoute.getAsBoolean() && entity.hasMoveDirection()) {
            // Clone prevents NPE caused by asynchronous
            Vector3 moveDirectionEnd = entity.getMoveDirectionEnd().clone();
            // Construct path direction vector
            var routeDirectionVector = new Vector3(moveDirectionEnd.x - entity.x, moveDirectionEnd.y - entity.y, moveDirectionEnd.z - entity.z);
            var yaw = BVector3.getYawFromVector(routeDirectionVector);
            entity.setYaw(yaw);
            if (!lookAtTarget.getAsBoolean()) {
                entity.setHeadYaw(yaw);
                if (entity.isEnablePitch()) entity.setPitch(BVector3.getPitchFromVector(routeDirectionVector));
            }
        }
        if (lookAtTarget.getAsBoolean() && lookTarget != null) {
            // Construct a vector pointing to the player
            var toPlayerVector = new Vector3(lookTarget.x - entity.x, lookTarget.y - entity.y, lookTarget.z - entity.z);
            if (entity.isEnablePitch()) entity.setPitch(BVector3.getPitchFromVector(toPlayerVector));
            entity.setHeadYaw(BVector3.getYawFromVector(toPlayerVector));
        }
        if (!entity.isEnablePitch()) entity.setPitch(0);
        return true;
    }
}
