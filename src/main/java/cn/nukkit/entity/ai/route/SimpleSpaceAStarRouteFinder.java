package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.blockevaluator.IBlockEvaluator;
import cn.nukkit.entity.ai.route.data.Node;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

    public SimpleSpaceAStarRouteFinder(IBlockEvaluator blockEvaluator, EntityIntelligent entity) {
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
                    var vec = node.getVector3().add(dx, dy, dz);
                    if (!existInCloseList(vec) && evalBlock(entity.level.getTickCachedBlock(vec))) {
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

    @Override
    protected ArrayList<Node> FloydSmooth(ArrayList<Node> array) {
        return super.FloydSmooth(array);
    }

//    @Override
//    protected boolean hasBarrier(Vector3 pos1, Vector3 pos2) {
//        var list = new ArrayList<Vector3>();
//        var rx = pos1.x;
//        var rz = pos1.z;
//        var ry = pos1.y;
//        var dx = pos2.x - rx;
//        var dz = pos2.z - rz;
//        var dy = pos2.y - ry;
//        double px;
//        double pz;
//        double py;
//        int cnt;
//        if (Math.abs(dx) > Math.abs(dz)) {
//            px = 1;
//            cnt = (int) Math.abs(dx);
//            pz = dz / cnt;
//        } else {
//            pz = 1;
//            cnt = (int) Math.abs(dz);
//            px = dx / cnt;
//        }
//        py = dy / cnt;
//        rx -= px;
//        rz -= pz;
//        ry -= py;
//        for (int i = 0; i < cnt; i++) {
//            rx += px;
//            rz += pz;
//            ry += py;
//            list.add(new Vector3(rx, ry, rz));
//        }
//        var tmp = hasBlocksAround(list);
//        return tmp;
//    }

    protected boolean hasBlocksAround(ArrayList<Vector3> list) {
        double radius = (this.entity.getWidth() * this.entity.getScale()) / 2 + 0.1;
        double height = this.entity.getHeight() * this.entity.getScale();
        for (Vector3 vector3 : list) {
            AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
            if (Utils.hasCollisionTickCachedBlocks(level, bb)) return true;
        }
        return false;
    }
}
