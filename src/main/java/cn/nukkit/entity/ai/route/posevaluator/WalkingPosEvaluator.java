package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

/**
 * 用于标准陆地行走实体的方块评估器
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class WalkingPosEvaluator implements IPosEvaluator {
    @Override
    public boolean evalPos(EntityIntelligent entity, Vector3 vec) {
        //居中坐标
        var blockCenter = vec.add(0.5, 0, 0.5);
        //检查是否可到达
        if (!isPassable(entity, blockCenter))
            return false;
        var block = entity.level.getTickCachedBlock(blockCenter.add(0, -1));
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

    @Override
    public boolean evalStandingBlock(EntityIntelligent entity, Block block) {
        //脚下不能是伤害性方块
        if (block.getId() == Block.FLOWING_LAVA || block.getId() == Block.STILL_LAVA || block.getId() == Block.CACTUS)
            return false;
        //水特判
        if(block.getId() == Block.STILL_WATER || block.getId() == Block.FLOWING_WATER)
            return true;
        //必须可以站立
        return !block.canPassThrough();
    }

    /**
     * 指定实体在指定坐标上能否不发生碰撞
     */
    protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
        double radius = (entity.getWidth() * entity.getScale()) / 2 + 0.1;
        float height = entity.getHeight() * entity.getScale();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        return !Utils.hasCollisionTickCachedBlocks(entity.level, bb);
    }
}
