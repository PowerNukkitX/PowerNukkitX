package org.powernukkitx.entity.ai.route.finder.impl;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.EntityAI;
import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.entity.ai.route.finder.SimpleRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.IPosEvaluator;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.particle.BlockForceFieldParticle;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Standard A* pathfinding implementation
 */


@Getter
@Setter
public class SimpleFlatAStarRouteFinder extends SimpleRouteFinder {

    //These constants are set to avoid square root operations
    //Straight move cost
    protected final static int DIRECT_MOVE_COST = 10;
    //Diagonal move cost
    protected final static int OBLIQUE_MOVE_COST = 14;

    protected final PriorityQueue<Node> openList = new PriorityQueue<>();

    protected final List<Node> closeList = new ArrayList<>();
    protected final HashSet<Vector3> closeHashSet = new HashSet<>();

    protected EntityIntelligent entity;

    protected Vector3 start;

    protected Vector3 target;

    protected Vector3 reachableTarget;

    protected boolean finished = false;
    protected boolean searching = false;

    protected boolean interrupt = false;

    protected boolean reachable = true;

    protected boolean enableFloydSmooth = true;

    //Maximum pathfinding depth
    protected int currentSearchDepth = 100;

    protected int maxSearchDepth = 100;

    protected long lastRouteParticleSpawn;

    public SimpleFlatAStarRouteFinder(IPosEvaluator blockEvaluator, EntityIntelligent entity) {
        super(blockEvaluator);
        this.entity = entity;
    }

    @Override
    public void setStart(Vector3 vector3) {
        this.start = vector3;
        if (isInterrupt()) this.setInterrupt(true);
    }

    @Override
    public void setTarget(Vector3 vector3) {
        this.target = vector3;
        if (isInterrupt()) this.setInterrupt(true);
    }

    @Override
    public boolean isSearching() {
        return searching;
    }

    @Override
    public boolean search() {
        //init status
        this.finished = false;
        this.searching = true;
        this.interrupt = false;
        var currentReachable = true;
        //If the entity is not active, disable path smoothing
        this.setEnableFloydSmooth(this.entity.isActive());
        //Clear openList and closeList
        openList.clear();
        closeList.clear();
        closeHashSet.clear();
        //Reset the pathfinding depth
        currentSearchDepth = maxSearchDepth;

        //Put the start point into closeList to begin pathfinding
        //The start point has no parent node, and we don't need to calculate its cost
        Node currentNode = new Node(start, null, 0, 0);
        var tmpNode = new Node(start, null, 0, 0);
        closeList.add(tmpNode);
        closeHashSet.add(tmpNode.getVector3());

        //While the current pathfinding point has not reached the target
        while (!isPositionOverlap(currentNode.getVector3(), target)) {
            //Check whether it was interrupted
            if (this.isInterrupt()) {
                currentSearchDepth = 0;
                this.searching = false;
                this.finished = true;
                this.reachable = false;
                return false;
            }
            //Put the valid nodes around the current node into openList
            putNeighborNodeIntoOpen(currentNode);
            //If the pathfinding depth is not exceeded, get the lowest-cost node and set it as currentNode
            if (openList.peek() != null && currentSearchDepth-- > 0) {
                closeList.add(currentNode = openList.poll());
                closeHashSet.add(currentNode.getVector3());
            } else {
                this.searching = false;
                this.finished = true;
                currentReachable = false;
                break;
            }
        }

        // The earlier "reached target" check only compares floored coordinates. Only append the
        // precise target if the entity-sized path from the safe node to it is actually clear.
        // Targets close to or inside a block must not turn into a final waypoint through a wall.
        Node targetNode = currentNode;
        if (!currentNode.getVector3().equals(target)
                && !hasBarrier(currentNode.getVector3(), target)) {
            targetNode = new Node(target, currentNode, 0, 0);
        }

        //If unreachable, take the Node closest to the target as the tail node
        Node reachableNode = null;
        reachableTarget = currentReachable ? target : (reachableNode = getNearestNodeFromCloseList(target)).getVector3();
        List<Node> findingPath = currentReachable ? getPathRoute(targetNode) : getPathRoute(reachableNode);
        //Smooth the path using Floyd
        if (enableFloydSmooth)
            findingPath = floydSmooth(findingPath);

        //Clear the previous pathfinding result
        this.resetNodes();
        //Reset the Node pointer
        this.setNodeIndex(0);

        //Write the result
        this.addNode(findingPath);

        if (EntityAI.checkDebugOption(EntityAI.DebugOption.ROUTE)) {
            if (System.currentTimeMillis() - lastRouteParticleSpawn > EntityAI.getRouteParticleSpawnInterval()) {
                findingPath.forEach(node -> this.entity.level.addParticle(new BlockForceFieldParticle(node.getVector3()), Server.getInstance().getOnlinePlayers().values().toArray(Player.EMPTY_ARRAY)));
                lastRouteParticleSpawn = System.currentTimeMillis();
            }
        }

        this.reachable = currentReachable;
        this.finished = true;
        this.searching = false;

        return true;
    }

    /**
     * Get the move cost of the block at the given position
     *
     * @param level
     * @param pos
     * @return cost
     */
    protected int getBlockMoveCostAt(@NotNull Level level, Vector3 pos) {
        return level.getTickCachedBlock(pos).getWalkThroughExtraCost() + level.getTickCachedBlock(pos.add(0, -1, 0)).getWalkThroughExtraCost();
    }

    /**
     * Put the valid nodes around a node into the OpenList
     *
     * @param node the node
     */
    protected void putNeighborNodeIntoOpen(@NotNull Node node) {
        boolean N, E, S, W;

        Vector3 vector3 = new Vector3(node.getVector3().getFloorX() + 0.5, node.getVector3().getY(), node.getVector3().getFloorZ() + 0.5);

        double offsetY;

        if ((offsetY = getAvailableHorizontalOffset(vector3)) != -384) {
            if (Math.abs(offsetY) > 0.25) {
                Vector3 vec = vector3.add(0, offsetY, 0);
                if (!existInCloseList(vec)) {
                    Node nodeNear = getOpenNode(vec);
                    if (nodeNear == null) {
                        this.openList.offer(new Node(vec, node, node.getG(), calH(vec, target)));
                    } else {
                        if (node.getG() < nodeNear.getG()) {
                            nodeNear.setParent(node);
                            nodeNear.setG(node.getG());
                            nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                        }
                    }
                }
            }
        }

        if (E = ((offsetY = getAvailableHorizontalOffset(vector3.add(1, 0, 0))) != -384)) {
            Vector3 vec = vector3.add(1, offsetY, 0);
            if (!existInCloseList(vec)) {
                var cost = getBlockMoveCostAt(entity.level, vec) + DIRECT_MOVE_COST + node.getG();
                Node nodeNear = getOpenNode(vec);
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

        if (S = ((offsetY = getAvailableHorizontalOffset(vector3.add(0, 0, 1))) != -384)) {
            Vector3 vec = vector3.add(0, offsetY, 1);
            if (!existInCloseList(vec)) {
                var cost = getBlockMoveCostAt(entity.level, vec) + DIRECT_MOVE_COST + node.getG();
                Node nodeNear = getOpenNode(vec);
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

        if (W = ((offsetY = getAvailableHorizontalOffset(vector3.add(-1, 0, 0))) != -384)) {
            Vector3 vec = vector3.add(-1, offsetY, 0);
            if (!existInCloseList(vec)) {
                var cost = getBlockMoveCostAt(entity.level, vec) + DIRECT_MOVE_COST + node.getG();
                Node nodeNear = getOpenNode(vec);
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

        if (N = ((offsetY = getAvailableHorizontalOffset(vector3.add(0, 0, -1))) != -384)) {
            Vector3 vec = vector3.add(0, offsetY, -1);
            if (!existInCloseList(vec)) {
                var cost = getBlockMoveCostAt(entity.level, vec) + DIRECT_MOVE_COST + node.getG();
                Node nodeNear = getOpenNode(vec);
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

        //We don't allow entities to move diagonally while going up or down slopes, because it easily causes them to get stuck (vanilla uses this logic too)
        //When touching water this check is no longer needed
        if (N && E && (((offsetY = getAvailableHorizontalOffset(vector3.add(1, 0, -1))) == 0) || (offsetY != -384 && entity.isTouchingWater()))) {
            Vector3 vec = vector3.add(1, offsetY, -1);
            if (!existInCloseList(vec)) {
                var cost = getBlockMoveCostAt(entity.level, vec) + OBLIQUE_MOVE_COST + node.getG();
                Node nodeNear = getOpenNode(vec);
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

        if (E && S && (((offsetY = getAvailableHorizontalOffset(vector3.add(1, 0, 1))) == 0) || (offsetY != -384 && entity.isTouchingWater()))) {
            Vector3 vec = vector3.add(1, offsetY, 1);
            if (!existInCloseList(vec)) {
                var cost = getBlockMoveCostAt(entity.level, vec) + OBLIQUE_MOVE_COST + node.getG();
                Node nodeNear = getOpenNode(vec);
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

        if (W && S && (((offsetY = getAvailableHorizontalOffset(vector3.add(-1, 0, 1))) == 0) || (offsetY != -384 && entity.isTouchingWater()))) {
            Vector3 vec = vector3.add(-1, offsetY, 1);
            if (!existInCloseList(vec)) {
                var cost = getBlockMoveCostAt(entity.level, vec) + OBLIQUE_MOVE_COST + node.getG();
                Node nodeNear = getOpenNode(vec);
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

        if (W && N && (((offsetY = getAvailableHorizontalOffset(vector3.add(-1, 0, -1))) == 0) || (offsetY != -384 && entity.isTouchingWater()))) {
            Vector3 vec = vector3.add(-1, offsetY, -1);
            if (!existInCloseList(vec)) {
                var cost = getBlockMoveCostAt(entity.level, vec) + OBLIQUE_MOVE_COST + node.getG();
                Node nodeNear = getOpenNode(vec);
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

    protected Node getOpenNode(Vector3 vector2) {
        for (Node node : this.openList) {
            if (vector2.equals(node.getVector3())) {
                return node;
            }
        }

        return null;
    }

    protected boolean existInOpenList(Vector3 vector2) {
        return getOpenNode(vector2) != null;
    }

    protected Node getCloseNode(Vector3 vector2) {
        for (Node node : this.closeList) {
            if (vector2.equals(node.getVector3())) {
                return node;
            }
        }
        return null;
    }

    protected boolean existInCloseList(Vector3 vector2) {
        return closeHashSet.contains(vector2);
    }

    /**
     * Calculate the cost H from the current point to the target
     * By default uses diagonal + straight-line distance
     */
    protected int calH(Vector3 start, Vector3 target) {
        //Calculate the cost using DIRECT_MOVE_COST and OBLIQUE_MOVE_COST
        //Calculate the diagonal distance
        int obliqueCost = (int) (Math.abs(Math.min(target.x - start.x, target.z - start.z)) * OBLIQUE_MOVE_COST);
        //Calculate the remaining straight-line distance
        int directCost = (int) ((Math.abs(Math.max(target.x - start.x, target.z - start.z)) - Math.abs(Math.min(target.x - start.x, target.z - start.z))) * DIRECT_MOVE_COST);
        return obliqueCost + directCost + (int) (Math.abs(target.y - start.y) * DIRECT_MOVE_COST);
    }

    /**
     * Get the highest valid point at the target coordinate (checking downwards along the Y axis)
     */
    public Block getHighestUnder(Vector3 vector3, int limit) {
        if (limit > 0) {
            for (int y = vector3.getFloorY(); y >= vector3.getFloorY() - limit; y--) {
                Block block = this.entity.level.getTickCachedBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
                if (evalStandingBlock(block))
                    return block;
            }
            return null;
        }
        for (int y = vector3.getFloorY(); y >= -64; y--) {
            Block block = this.entity.level.getTickCachedBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
            if (evalStandingBlock(block))
                return block;
        }
        return null;
    }

    /**
     * Whether the given position can serve as a valid node
     */
    protected boolean evalPos(Vector3 pos) {
        return evalPos.evalPos(entity, pos);
    }

    /**
     * Whether the space above the given block can serve as a valid node
     */
    protected boolean evalStandingBlock(Block block) {
        return evalPos.evalStandingBlock(entity, block);
    }

    /**
     * @param vector3
     * @return the highest reachable point at the given coordinate (limit=4)
     */
    protected int getAvailableHorizontalOffset(Vector3 vector3) {
        var block = getHighestUnder(vector3, 4);
        if (block != null) {
            return block.getFloorY() - vector3.getFloorY() + 1;
        }
        return -384;
    }

    protected boolean hasBarrier(Node node1, Node node2) {
        return hasBarrier(node1.getVector3(), node2.getVector3());
    }

    /**
     * Whether there is an obstacle between the two given Nodes
     */
    protected boolean hasBarrier(Vector3 pos1, Vector3 pos2) {
        if (pos1.equals(pos2)) return false;

        double dx = pos2.x - pos1.x;
        double dy = pos2.y - pos1.y;
        double dz = pos2.z - pos1.z;
        int samples = Math.max(1, (int) Math.ceil(Math.sqrt(dx * dx + dy * dy + dz * dz) * 4));
        double radius = entity.getWidth() * entity.getScale() * 0.5;
        double height = entity.getHeight() * entity.getScale();

        for (int i = 0; i <= samples; i++) {
            double progress = (double) i / samples;
            double x = pos1.x + dx * progress;
            double y = pos1.y + dy * progress;
            double z = pos1.z + dz * progress;

            if (!evalStandingBlock(entity.level.getTickCachedBlock(
                    (int) Math.floor(x),
                    (int) Math.floor(y - 1),
                    (int) Math.floor(z),
                    false
            ))) {
                return true;
            }

            AxisAlignedBB entityBox = new SimpleAxisAlignedBB(
                    x - radius, y, z - radius,
                    x + radius, y + height, z + radius
            );
            if (Utils.hasCollisionTickCachedBlocks(entity.level, entityBox)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Smooth the A* path using the Floyd algorithm
     */
    protected List<Node> floydSmooth(List<Node> array) {
        int current = 0;
        int total = 2;
        if (array.size() > 2) {
            while (total < array.size()) {
                if (hasBarrier(array.get(current), array.get(total)) || total == array.size() - 1) {
                    array.get(total - 1).setParent(array.get(current));
                    current = total - 1;
                }
                total++;
            }
            Node temp = array.get(array.size() - 1);
            List<Node> tempL = new ArrayList<>();
            tempL.add(temp);
            while (temp.getParent() != null && !temp.getParent().getVector3().equals(start)) {
                tempL.add((temp = temp.getParent()));
            }
            Collections.reverse(tempL);
            return tempL;
        }
        return array;
    }

    /**
     * Convert the Node chain into path information in List<Node> form
     *
     * @param end the tail node of the list
     */
    @SuppressWarnings("null")
    protected List<Node> getPathRoute(@Nullable Node end) {
        List<Node> nodes = new ArrayList<>();
        if (end == null)
            end = closeList.get(closeList.size() - 1);
        nodes.add(end);
        if (end.getParent() != null) {
            while (!end.getParent().getVector3().equals(start)) {
                nodes.add(end = end.getParent());
            }
        } else {
            nodes.add(end);
        }
        Collections.reverse(nodes);
        return nodes;

    }

    /**
     * Get the Node closest to the given coordinate
     */
    protected Node getNearestNodeFromCloseList(Vector3 vector3) {
        double min = Double.MAX_VALUE;
        Node node = null;
        for (Node n : closeList) {
            double distanceSquared = n.getVector3().floor().distanceSquared(vector3.floor());
            if (distanceSquared < min) {
                min = distanceSquared;
                node = n;
            }
        }
        return node;
    }

    /**
     * Whether the coordinates overlap <br/>
     * This method only compares the floorX, floorY and floorZ of the coordinates
     */
    protected boolean isPositionOverlap(Vector3 vector2, Vector3 vector2_) {
        return vector2.getFloorX() == vector2_.getFloorX()
                && vector2.getFloorZ() == vector2_.getFloorZ()
                && vector2.getFloorY() == vector2_.getFloorY();
    }
}
