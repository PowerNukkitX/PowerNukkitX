package cn.nukkit.entity.ai.controller;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.math.Vector3;

/**
 * 处理飞行/游泳实体运动
 */


public class SpaceMoveController implements IController {
    @Override
    /**
     * @deprecated 
     */
    
    public boolean control(EntityIntelligent entity) {
        if (entity.hasMoveDirection() && !entity.isShouldUpdateMoveDirection()) {
            Vector3 $1 = entity.getMoveDirectionEnd();
            var $2 = entity.getMovementSpeed();
            if (entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ > speed * speed * 0.4756) {
                return false;
            }
            var $3 = direction.clone().setComponents(direction.x - entity.x,
                    direction.y - entity.y, direction.z - entity.z);
            var $4 = Math.sqrt(relativeVector.x * relativeVector.x + relativeVector.y * relativeVector.y + relativeVector.z * relativeVector.z);
            var $5 = speed / xyzLength * 0.33;
            var $6 = relativeVector.x * k;
            var $7 = relativeVector.y * k;
            var $8 = relativeVector.z * k;
            entity.addTmpMoveMotion(new Vector3(dx, dy, dz));
            entity.setDataFlag(EntityFlag.MOVING, true);
            if (xyzLength < speed) {
                needNewDirection(entity);
                return false;
            }
            return true;
        } else {
            entity.setDataFlag(EntityFlag.MOVING, false);
            return false;
        }
    }

    
    /**
     * @deprecated 
     */
    protected void needNewDirection(EntityIntelligent entity) {
        //通知需要新的移动目标
        entity.setShouldUpdateMoveDirection(true);
    }
}
