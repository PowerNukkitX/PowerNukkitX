package cn.nukkit.entity.ai.route.blockevaluator;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

/**
 * 用于标准陆地行走实体的方块评估器
 */
public class OnGroundBlockEvaluator implements IBlockEvaluator {
    @Override
    public boolean evalBlock(EntityIntelligent entity, Block block) {
        //居中坐标
        Vector3 blockCenter = block.add(0.5, 0, 0.5);
        //检查是否可到达
        if (!isPassable(entity, blockCenter.add(0, 1, 0)))
            return false;
        //TODO: 检查碰头
        //脚下不能是伤害性方块
        if (block.getId() == Block.FLOWING_LAVA || block.getId() == Block.STILL_LAVA || block.getId() == Block.CACTUS)
            return false;
        //水特判
        if(block.getId() == Block.STILL_WATER || block.getId() == Block.FLOWING_WATER)
            return true;
        //必须可以站立
        if (block.canPassThrough())
            return false;
        return true;
    }

    /**
     * 指定实体在指定坐标上能否不发生碰撞
     */
    protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
        double radius = (entity.getWidth() * entity.getScale()) / 2;
        float height = entity.getHeight() * entity.getScale();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        return !Utils.hasCollisionTickCachedBlocks(entity.level, bb)/* && !this.level.getBlock(vector3.add(0, -1, 0), false).canPassThrough()*/;
    }
}
