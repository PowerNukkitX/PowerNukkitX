package cn.nukkit.entity.ai.route.blockevaluator;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

public class OnGroundBlockEvaluator implements IBlockEvaluator {
    @Override
    public int evalBlock(EntityIntelligent entity,Block block) {
        //检查是否可到达
        if (!block.add(0,1,0).getLevelBlock().canPassThrough())
            return -1;
//        //若y轴有变化，我们需要检查能否移动到那里
//        if ((block.y + 1) > entity.y){
//            for (double tmpY = entity.y; tmpY <= (block.y + 1); tmpY+=1){
//                if (!isPassable(entity,new Vector3(entity.x,tmpY,entity.z)))
//                    return -1;
//            }
//        }
        //脚下不能是伤害性方块
        if (block.getId() == Block.FLOWING_LAVA || block.getId() == Block.STILL_LAVA || block.getId() == Block.CACTUS)
            return -1;
        //水的H较大
        if (block.getId() == Block.FLOWING_WATER || block.getId() == Block.STILL_WATER)
            return 10;//排除水
        //必须可以站立
        if (block.canPassThrough())
            return -1;
        //默认权重
        return 1;
    }

    /**
     * 指定实体在指定坐标上能否不发生碰撞
     */
    protected boolean isPassable(EntityIntelligent entity,Vector3 vector3) {
        double radius = (entity.getWidth() * entity.getScale()) / 2;
        float height = entity.getHeight() * entity.getScale();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        return !Utils.hasCollisionBlocks(entity.level, bb)/* && !this.level.getBlock(vector3.add(0, -1, 0), false).canPassThrough()*/;
    }
}
