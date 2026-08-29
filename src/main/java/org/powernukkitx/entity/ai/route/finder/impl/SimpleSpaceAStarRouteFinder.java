package org.powernukkitx.entity.ai.route.finder.impl;

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
    }

    @Override
    protected int getBlockMoveCostAt(@NotNull Level level, Vector3 pos) {
        return level.getTickCachedBlock(pos.add(0, -1, 0)).getWalkThroughExtraCost();
    }

    @Override
    protected void putNeighborNodeIntoOpen(@NotNull Node node) {
        var centeredNode = node.getVector3().floor().add(0.5, 0.5, 0.5);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    var vec = centeredNode.add(dx, dy, dz);
                    if (!existInCloseList(vec) && evalPos(vec)) {
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

    /**
     * Determines whether there is an obstacle between two Nodes
     */
    protected boolean hasBarrier(Vector3 pos1, Vector3 pos2) {
        if (pos1.equals(pos2)) return false;
        for (Vector3 pos : VectorMath.getPassByVector3(pos1, pos2)) {
            if (!evalPos(this.entity.level.getTickCachedBlock(pos.add(0, -1)))) {
                return true;
            }
        }
        return false;
    }
}
