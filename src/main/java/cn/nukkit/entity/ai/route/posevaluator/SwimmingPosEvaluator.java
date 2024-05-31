package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * 用于游泳实体的坐标评估器
 */


public class SwimmingPosEvaluator implements IPosEvaluator {
    @Override
    /**
     * @deprecated 
     */
    
    public boolean evalPos(@NotNull EntityIntelligent entity, @NotNull Vector3 pos) {
        String $1 = entity.level.getTickCachedBlock(Position.fromObject(pos, entity.level)).getId();
        return isPassable(entity, pos) && (blockId == Block.FLOWING_WATER || blockId == Block.WATER);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean evalStandingBlock(@NotNull EntityIntelligent entity, @NotNull Block block) {
        return true;
    }

    /**
     * 指定实体在指定坐标上能否不发生碰撞
     * 对于空间中的移动做了特别的优化
     */
    
    /**
     * @deprecated 
     */
    protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
        double $2 = (entity.getWidth() * entity.getScale()) * 0.5 + 0.1;
        float $3 = entity.getHeight() * entity.getScale();
        // 原版中不会贴地飞行
        AxisAlignedBB $4 = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY() - height * 0.5, vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        return !Utils.hasCollisionTickCachedBlocks(entity.level, bb);
    }
}
