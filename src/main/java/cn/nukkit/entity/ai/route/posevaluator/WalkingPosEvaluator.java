package cn.nukkit.entity.ai.route.posevaluator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFence;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * 用于标准陆地行走实体的方块评估器
 */


public class WalkingPosEvaluator implements IPosEvaluator {
    @Override
    public boolean evalStandingBlock(@NotNull EntityIntelligent entity, @NotNull Block block) {
        //居中坐标
        var blockCenter = block.add(0.5, 1, 0.5);
        //检查是否可到达
        if (!isPassable(entity, blockCenter))
            return false;
        if (entity.hasWaterAt(0) && blockCenter.getY() - entity.getY() > 1)//实体在水中不能移动到一格高以上的方块
            return false;
        //TODO: 检查碰头
        //脚下不能是伤害性方块
        if (block.getId() == Block.FLOWING_LAVA || block.getId() == Block.LAVA || block.getId() == Block.CACTUS)
            return false;
        //不能是栏杆
        if (block instanceof BlockFence || block instanceof BlockFenceGate)
            return false;
        //水特判
        if (block.getId() == Block.WATER || block.getId() == Block.FLOWING_WATER)
            return true;
        //必须可以站立
        return !block.canPassThrough();
    }

    /**
     * 指定实体在指定坐标上能否不发生碰撞
     */
    //todo: 此方法会造成大量开销，原因是碰撞检查，有待优化
    protected boolean isPassable(EntityIntelligent entity, Vector3 vector3) {
        double radius = (entity.getWidth() * entity.getScale()) / 2;
        float height = entity.getHeight() * entity.getScale();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        if (radius > 0.5) {
            // A --- B --- C
            // |           |
            // D     P     E
            // |           |
            // F --- G --- H
            // 在P点一次通过的可能性最大，所以优先检测
            byte collisionInfo = Utils.hasCollisionTickCachedBlocksWithInfo(entity.level, bb);
            if (collisionInfo == 0) {
                return true;
            }
            // 将实体碰撞箱分别对齐A B C D E F G H处，检测是否能通过
            var dr = radius - 0.5;
            for (int i = -1; i <= 1; i++) {
                // collisionInfo & 0b110000：获取x轴的碰撞信息，3为在大于中心的方向膨胀，1为小于中心的方向碰撞，0为没有碰撞
                // -2：是为了将3转换为1，1转换为-1，0转换为-2
                // 然后进行判断，如果i的值跟上面的值相等，说明此方向已经100%会碰撞了，不需要再检测
                if (((collisionInfo & 0b110000) >> 4) - 2 == i) continue;
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue; // P点已经被检测过了
                    if ((collisionInfo & 0b000011) - 2 == j) continue; // 获取z轴的碰撞信息并比较
                    // 由于已经缓存了方块，检测速度还是可以接受的
                    if (!Utils.hasCollisionTickCachedBlocks(entity.level, bb.clone().offset(i * dr, 0, j * dr))) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return !Utils.hasCollisionTickCachedBlocks(entity.level, bb);
        }
    }
}
