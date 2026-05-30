package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.block.BlockWoodenDoor;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;

public class DoorCapableWalkingPosEvaluator extends WalkingPosEvaluator {

    @Override
    protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
        if (super.isPassable(entity, vector3)) return true;
        double radius = (entity.getWidth() * entity.getScale()) / 2;
        float height = entity.getHeight() * entity.getScale();
        var bb = new SimpleAxisAlignedBB(
                vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius,
                vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        return entity.level.getCollisionBlocks(bb, false, false,
                b -> !(b instanceof BlockWoodenDoor)).length == 0;
    }
}
