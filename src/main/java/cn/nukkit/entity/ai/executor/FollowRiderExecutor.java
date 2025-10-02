package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;

public class FollowRiderExecutor implements IBehaviorExecutor, EntityControl {

    @Override
    public boolean execute(EntityIntelligent entity) {
        if(entity instanceof EntityRideable) {
            Entity riding = entity.getPassenger();
            if(riding == null) return false;
            Vector2 direction = riding.getDirectionPlane().normalize();
            Vector3 whereToGo = entity.asVector3f().asVector3().add(direction.x, 0, direction.y);
            for(int i = -1; i <= 1; i++) {
                Block block = entity.getLevel().getBlock(whereToGo.add(0, i, 0));
                if(block.canPassThrough()) {
                    whereToGo = whereToGo.add(0, i, 0);
                    break;
                }
            }
            setLookTarget(entity, whereToGo);
            setRouteTarget(entity, whereToGo);
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            return true;
        }
        return false;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.setMovementSpeed(0.25f);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setMovementSpeed(entity.getDefaultSpeed());
    }
}
