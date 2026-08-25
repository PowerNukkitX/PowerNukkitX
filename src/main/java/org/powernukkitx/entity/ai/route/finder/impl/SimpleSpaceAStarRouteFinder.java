package org.powernukkitx.entity.ai.route.finder.impl;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.entity.ai.route.posevaluator.IPosEvaluator;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.VectorMath;
import org.jetbrains.annotations.NotNull;

/**
 * Be aware that standard 3D A* pathfinding is very expensive (much lower than vanilla's flood fill), so never set the max pathfinding depth too large!
 * TODO: replace with BA*, JPS or potential-field pathfinding
 */


public class SimpleSpaceAStarRouteFinder extends SimpleFlatAStarRouteFinder {
    //Straight move cost
    protected final static int DIRECT_MOVE_COST = 10;
    //Diagonal move cost
    protected final static int OBLIQUE_2D_MOVE_COST = 14;
    protected final static int OBLIQUE_3D_MOVE_COST = 17;

    public SimpleSpaceAStarRouteFinder(IPosEvaluator blockEvaluator, EntityIntelligent entity) {
        super(blockEvaluator, entity);
        // 3D A* expands 26 neighbours per node instead of 8, so the default depth of 100 costs
        // roughly three times as much per search as it does for the flat finder.
        setMaxSearchDepth(40);
    }

    @Override
    protected int getBlockMoveCostAt(@NotNull Level level, Vector3 pos) {
        Block below = level.getTickCachedBlock(pos.getFloorX(), pos.getFloorY() - 1, pos.getFloorZ(), 0, false);
        return below == null ? 0 : below.getWalkThroughExtraCost();
    }

    @Override
    protected void putNeighborNodeIntoOpen(@NotNull Node node) {
        var centeredNode = node.getVector3().floor().add(0.5, 0.5, 0.5);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    var vec = centeredNode.add(dx, dy, dz);
                    // centeredNode is grid aligned and the offsets are integers, so the memoised
                    // variant is safe here.
                    if (!existInCloseList(vec) && evalGridPos(vec)) {
                        // Calculate the cost of moving 1 block
                        var cost = switch (Math.abs(dx) + Math.abs(dy) + Math.abs(dz)) {
                            case 1 -> DIRECT_MOVE_COST;
                            case 2 -> OBLIQUE_2D_MOVE_COST;
                            case 3 -> OBLIQUE_3D_MOVE_COST;
                            default -> Integer.MIN_VALUE;
                        } + getBlockMoveCostAt(this.entity.level, vec) + node.getG() - dy; // -dy biases toward flying through the air rather than hugging the ground
                        if (cost < 0) continue;
                        var nodeNear = getOpenNode(vec);
                        if (nodeNear == null) {
                            offerOpenNode(new Node(vec, node, cost, calH(vec, target)));
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

    /**
     * Determines whether there is an obstacle between two Nodes.
     *
     * <p>Reads with {@code load = false} so path smoothing cannot pull chunks in from disk on a
     * pathfinding thread. An unloaded position is treated as a barrier, which is the conservative
     * choice.
     */
    @Override
    protected boolean hasBarrier(Vector3 pos1, Vector3 pos2) {
        if (pos1.equals(pos2)) return false;
        for (Vector3 pos : VectorMath.getPassByVector3(pos1, pos2)) {
            Block below = this.entity.level.getTickCachedBlock(
                    pos.getFloorX(), pos.getFloorY() - 1, pos.getFloorZ(), 0, false);
            if (below == null || !evalPos(below)) {
                return true;
            }
        }
        return false;
    }
}
