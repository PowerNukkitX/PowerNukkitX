package cn.nukkit.entity.ai.route.blockevaluator;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class FlyingBlockEvaluator implements IBlockEvaluator{
    @Override
    public boolean evalBlock(EntityIntelligent entity, Block block) {
        //居中坐标
        Position blockCenter = block.add(0.5, 0, 0.5);
        //检查是否可到达
        if (!isPassable(entity, blockCenter.add(0, 1, 0)))
            return false;
        if(blockCenter.add(0, 1, 0).getLevelBlock().getId() != 0)
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
        return !Utils.hasCollisionTickCachedBlocks(entity.level, bb);
    }
}
