package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

/**
 * 用于游泳实体的坐标评估器
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SwimmingPosEvaluator implements IPosEvaluator{
    @Override
    public boolean evalPos(EntityIntelligent entity, Vector3 pos) {
        int blockId = Position.fromObject(pos,entity.level).getLevelBlock().getId();
        return isPassable(entity, pos) && (blockId == Block.FLOWING_WATER || blockId == Block.STILL_WATER);
    }

    /**
     * 指定实体在指定坐标上能否不发生碰撞
     * 对于空间中的移动做了特别的优化
     */
    protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
        double radius = (entity.getWidth() * entity.getScale()) * 0.5 + 0.1;
        float height = entity.getHeight() * entity.getScale();
        // 原版中不会贴地飞行
        AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY() - height * 0.5, vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        return !Utils.hasCollisionTickCachedBlocks(entity.level, bb);
    }
}
