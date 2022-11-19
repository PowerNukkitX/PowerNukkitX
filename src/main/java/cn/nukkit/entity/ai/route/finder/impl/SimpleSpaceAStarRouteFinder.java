package cn.nukkit.entity.ai.route.finder.impl;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.data.Node;
import cn.nukkit.entity.ai.route.posevaluator.IPosEvaluator;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

/**
 * 务必注意，三维标准A*寻路的代价十分高昂(比原版的洪水填充低得多)，切忌将最大寻路深度设置得太大！
 * TODO: 用BA*、JPS或者势能场寻路代替
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class SimpleSpaceAStarRouteFinder extends SimpleFlatAStarRouteFinder {
    //直接移动成本
    protected final static int DIRECT_MOVE_COST = 10;
    //倾斜移动成本
    protected final static int OBLIQUE_2D_MOVE_COST = 14;
    protected final static int OBLIQUE_3D_MOVE_COST = 17;

    public SimpleSpaceAStarRouteFinder(IPosEvaluator blockEvaluator, EntityIntelligent entity) {
        super(blockEvaluator, entity);
    }

    @Override
    protected int getBlockMoveCostAt(@NotNull Level level, Vector3 pos) {
        return level.getTickCachedBlock(pos.add(0, -1, 0)).getWalkThroughExtraCost();
    }

    @Override
    protected void putNeighborNodeIntoOpen(@NotNull Node node) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    //居中防止卡角
                    var vec = node.getVector3().floor().add(dx + 0.5, dy + 0.5, dz + 0.5);
                    if (!existInCloseList(vec) && evalPos(vec)) {
                        // 计算移动1格的开销
                        var cost = switch (Math.abs(dx) + Math.abs(dy) + Math.abs(dz)) {
                            case 1 -> DIRECT_MOVE_COST;
                            case 2 -> OBLIQUE_2D_MOVE_COST;
                            case 3 -> OBLIQUE_3D_MOVE_COST;
                            default -> Integer.MIN_VALUE;
                        } + getBlockMoveCostAt(level, vec) + node.getG() - dy; // -dy是为了倾向于从空中飞而不是贴地飞
                        if (cost < 0) continue;
                        var nodeNear = getOpenNode(vec);
                        if (nodeNear == null) {
                            this.openList.offer(new Node(vec, node, cost, calH(vec, target)));
                        } else {
                            if (cost < nodeNear.getG()) {
                                nodeNear.setParent(node);
                                nodeNear.setG(cost);
                                nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                            }
                        }
                    }
                }
            }
        }
    }

//    @Override
//    protected boolean hasBarrier(Vector3 pos1, Vector3 pos2) {
//        return hasBlocksAround(VectorMath.getPassByVector3(pos1, pos2));
//    }
//
//    @Override
//    protected boolean hasBlocksAround(List<Vector3> list) {
//        double radius = (this.entity.getWidth() * this.entity.getScale()) / 2 + 0.1;
//        double height = this.entity.getHeight() * this.entity.getScale();
//        for (Vector3 vector3 : list) {
//            AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
//            if (Utils.hasCollisionTickCachedBlocks(level, bb)) return true;
//
//        }
//        return false;
//    }
}
