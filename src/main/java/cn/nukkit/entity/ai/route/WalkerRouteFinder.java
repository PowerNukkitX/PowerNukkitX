package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * @author zzz1999 @ MobPlugin
 */

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class WalkerRouteFinder extends SimpleRouteFinder {

    private final static int DIRECT_MOVE_COST = 10;
    private final static int OBLIQUE_MOVE_COST = 14;

    private final PriorityQueue<Node> openList = new PriorityQueue<>();
    private final ArrayList<Node> closeList = new ArrayList<>();

    private Class<?> targetMemoryClazz = null;

    private int searchLimit = 100;

    public WalkerRouteFinder(EntityIntelligent entity) {
        super(entity);
        this.level = entity.getLevel();
    }

    public WalkerRouteFinder(EntityIntelligent entity, Vector3 start) {
        super(entity);
        this.level = entity.getLevel();
        this.start = start;
    }

    public WalkerRouteFinder(EntityIntelligent entity, Vector3 start, Vector3 destination) {
        super(entity);
        this.level = entity.getLevel();
        this.start = start;
        this.destination = destination;
    }

    public WalkerRouteFinder(EntityIntelligent entity, Vector3 start, Class<?> targetMemoryClazz) {
        super(entity);
        this.level = entity.getLevel();
        this.start = start;
        this.targetMemoryClazz = targetMemoryClazz;
    }

    private int calHeuristic(Vector3 pos1, Vector3 pos2) {
        return 10 * (Math.abs(pos1.getFloorX() - pos2.getFloorX()) + Math.abs(pos1.getFloorZ() - pos2.getFloorZ())) +
                11 * Math.abs(pos1.getFloorY() - pos2.getFloorY());
    }

    @Override
    public boolean search() {

        this.finished = false;
        this.searching = true;

        if (this.start == null) {
            this.start = this.entity;
        }

        if (this.destination == null) {
            Vector3 vec = null;
            if (targetMemoryClazz != null)
                vec = (Vector3) entity.getBehaviorGroup().getMemory().get(targetMemoryClazz).getData();
            if (vec != null) {
                this.destination = vec;
            } else {
                this.searching = false;
                this.finished = true;
                return false;
            }
        }

        this.resetTemporary();

        Node presentNode = new Node(start);
        closeList.add(new Node(start));


        while (!isPositionOverlap(presentNode.getVector3(), destination)) {
            if (this.isInterrupted()) {
                searchLimit = 0;
                this.searching = false;
                this.finished = true;
                return false;
            }
            putNeighborNodeIntoOpen(presentNode);
            if (openList.peek() != null && searchLimit-- > 0) {
                closeList.add(presentNode = openList.poll());

            } else {
                this.searching = false;
                this.finished = true;
                this.reachable = false;
                this.addNode(new Node(destination));
                return false;
            }

        }

        if (!presentNode.getVector3().equals(destination)) {
            closeList.add(new Node(destination, presentNode, 0, 0));
        }
        ArrayList<Node> findingPath = getPathRoute();
        findingPath = FloydSmooth(findingPath);

        this.resetNodes();

        this.addNode(findingPath);
        this.finished = true;
        this.searching = false;

        return true;
    }


    private Block getHighestUnder(Vector3 vector3, int limit) {
        if (limit > 0) {
            for (int y = vector3.getFloorY(); y >= vector3.getFloorY() - limit; y--) {
                Block block = this.level.getBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
                if (isWalkable(block) && level.getBlock(block.add(0, 1, 0), false).getId() == Block.AIR) return block;
            }
            return null;
        }
        for (int y = vector3.getFloorY(); y >= 0; y--) {
            Block block = this.level.getBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
            if (isWalkable(block) && level.getBlock(block.add(0, 1, 0), false).getId() == Block.AIR) return block;
        }
        return null;
    }

    private boolean canWalkOn(Block block) {
        return !(block.getId() == Block.LAVA || block.getId() == Block.STILL_LAVA || block.getId() == Block.CACTUS);
    }

    private boolean isWalkable(Vector3 vector3) {
        Block block = level.getBlock(vector3, false);
        return !block.canPassThrough() && canWalkOn(block);
    }

    private boolean isPassable(Vector3 vector3) {
        double radius = (this.entity.getWidth() * this.entity.getScale()) / 2;
        float height = this.entity.getHeight() * this.entity.getScale();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
        return !Utils.hasCollisionBlocks(this.level, bb) && !this.level.getBlock(vector3.add(0, -1, 0), false).canPassThrough();
    }

    private int getWalkableHorizontalOffset(Vector3 vector3) {
        Block block = getHighestUnder(vector3, 4);
        if (block != null) {
            return (block.getFloorY() - vector3.getFloorY()) + 1;
        }
        return -256;
    }

    public int getSearchLimit() {
        return searchLimit;
    }

    public void setSearchLimit(int limit) {
        this.searchLimit = limit;
    }

    public void setTargetMemoryClazz(Class<?> clazz) {
        this.targetMemoryClazz = clazz;
    }

    public Class<?> getTargetMemoryClazz() {
        return this.targetMemoryClazz;
    }

    private void putNeighborNodeIntoOpen(Node node) {
        boolean N, E, S, W;

        Vector3 vector3 = new Vector3(node.getVector3().getFloorX() + 0.5, node.getVector3().getY(), node.getVector3().getFloorZ() + 0.5);

        double y;

        if (E = ((y = getWalkableHorizontalOffset(vector3.add(1, 0, 0))) != -256)) {
            Vector3 vec = vector3.add(1, y, 0);
            if (isPassable(vec) && !isContainsInClose(vec)) {
                Node nodeNear = getNodeInOpenByVector2(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, DIRECT_MOVE_COST + node.getG(), calHeuristic(vec, destination)));
                } else {
                    if (node.getG() + DIRECT_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (S = ((y = getWalkableHorizontalOffset(vector3.add(0, 0, 1))) != -256)) {
            Vector3 vec = vector3.add(0, y, 1);
            if (isPassable(vec) && !isContainsInClose(vec)) {
                Node nodeNear = getNodeInOpenByVector2(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, DIRECT_MOVE_COST + node.getG(), calHeuristic(vec, destination)));
                } else {
                    if (node.getG() + DIRECT_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (W = ((y = getWalkableHorizontalOffset(vector3.add(-1, 0, 0))) != -256)) {
            Vector3 vec = vector3.add(-1, y, 0);
            if (isPassable(vec) && !isContainsInClose(vec)) {
                Node nodeNear = getNodeInOpenByVector2(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, DIRECT_MOVE_COST + node.getG(), calHeuristic(vec, destination)));
                } else {
                    if (node.getG() + DIRECT_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (N = ((y = getWalkableHorizontalOffset(vector3.add(0, 0, -1))) != -256)) {
            Vector3 vec = vector3.add(0, y, -1);
            if (isPassable(vec) && !isContainsInClose(vec)) {
                Node nodeNear = getNodeInOpenByVector2(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, DIRECT_MOVE_COST + node.getG(), calHeuristic(vec, destination)));
                } else {
                    if (node.getG() + DIRECT_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (N && E && ((y = getWalkableHorizontalOffset(vector3.add(1, 0, -1))) != -256)) {
            Vector3 vec = vector3.add(1, y, -1);
            if (isPassable(vec) && !isContainsInClose(vec)) {
                Node nodeNear = getNodeInOpenByVector2(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, OBLIQUE_MOVE_COST + node.getG(), calHeuristic(vec, destination)));
                } else {
                    if (node.getG() + OBLIQUE_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (E && S && ((y = getWalkableHorizontalOffset(vector3.add(1, 0, 1))) != -256)) {
            Vector3 vec = vector3.add(1, y, 1);
            if (isPassable(vec) && !isContainsInClose(vec)) {
                Node nodeNear = getNodeInOpenByVector2(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, OBLIQUE_MOVE_COST + node.getG(), calHeuristic(vec, destination)));
                } else {
                    if (node.getG() + OBLIQUE_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (W && S && ((y = getWalkableHorizontalOffset(vector3.add(-1, 0, 1))) != -256)) {
            Vector3 vec = vector3.add(-1, y, 1);
            if (isPassable(vec) && !isContainsInClose(vec)) {
                Node nodeNear = getNodeInOpenByVector2(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, OBLIQUE_MOVE_COST + node.getG(), calHeuristic(vec, destination)));
                } else {
                    if (node.getG() + OBLIQUE_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (W && N && ((y = getWalkableHorizontalOffset(vector3.add(-1, 0, -1))) != -256)) {
            Vector3 vec = vector3.add(-1, y, -1);
            if (isPassable(vec) && !isContainsInClose(vec)) {
                Node nodeNear = getNodeInOpenByVector2(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, OBLIQUE_MOVE_COST + node.getG(), calHeuristic(vec, destination)));
                } else {
                    if (node.getG() + OBLIQUE_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }
    }

    private Node getNodeInOpenByVector2(Vector3 vector2) {
        for (Node node : this.openList) {
            if (vector2.equals(node.getVector3())) {
                return node;
            }
        }

        return null;
    }

    private boolean isContainsInOpen(Vector3 vector2) {
        return getNodeInOpenByVector2(vector2) != null;
    }

    private Node getNodeInCloseByVector2(Vector3 vector2) {
        for (Node node : this.closeList) {
            if (vector2.equals(node.getVector3())) {
                return node;
            }
        }
        return null;
    }

    private boolean isContainsInClose(Vector3 vector2) {
        return getNodeInCloseByVector2(vector2) != null;
    }

    private boolean hasBarrier(Node node1, Node node2) {
        return hasBarrier(node1.getVector3(), node2.getVector3());
    }

    private boolean hasBarrier(Vector3 pos1, Vector3 pos2) {
        if (pos1.equals(pos2)) return false;
        if (pos1.getFloorY() != pos2.getFloorY()) return true;
        boolean traverseDirection = Math.abs(pos1.getX() - pos2.getX()) > Math.abs(pos1.getZ() - pos2.getZ());
        if (traverseDirection) {
            double loopStart = Math.min(pos1.getX(), pos2.getX());
            double loopEnd = Math.max(pos1.getX(), pos2.getX());
            ArrayList<Vector3> list = new ArrayList<>();
            for (double i = Math.ceil(loopStart); i <= Math.floor(loopEnd); i += 1.0) {
                double result;
                if ((result = Utils.calLinearFunction(pos1, pos2, i, Utils.ACCORDING_X_OBTAIN_Y)) != Double.MAX_VALUE)
                    list.add(new Vector3(i, pos1.getY(), result));
            }
            return hasBlocksAround(list);
        } else {
            double loopStart = Math.min(pos1.getZ(), pos2.getZ());
            double loopEnd = Math.max(pos1.getZ(), pos2.getZ());
            ArrayList<Vector3> list = new ArrayList<>();
            for (double i = Math.ceil(loopStart); i <= Math.floor(loopEnd); i += 1.0) {
                double result;
                if ((result = Utils.calLinearFunction(pos1, pos2, i, Utils.ACCORDING_Y_OBTAIN_X)) != Double.MAX_VALUE)
                    list.add(new Vector3(result, pos1.getY(), i));
            }

            return hasBlocksAround(list);
        }

    }


    private boolean hasBlocksAround(ArrayList<Vector3> list) {
        double radius = (this.entity.getWidth() * this.entity.getScale()) / 2 + 0.1;
        double height = this.entity.getHeight() * this.entity.getScale();
        for (Vector3 vector3 : list) {
            AxisAlignedBB bb = new SimpleAxisAlignedBB(vector3.getX() - radius, vector3.getY(), vector3.getZ() - radius, vector3.getX() + radius, vector3.getY() + height, vector3.getZ() + radius);
            if (Utils.hasCollisionBlocks(level, bb)) return true;

            boolean xIsInt = vector3.getX() % 1 == 0;
            boolean zIsInt = vector3.getZ() % 1 == 0;
            if (xIsInt && zIsInt) {
                if (level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ()), false).canPassThrough() ||
                        level.getBlock(new Vector3(vector3.getX() - 1, vector3.getY() - 1, vector3.getZ()), false).canPassThrough() ||
                        level.getBlock(new Vector3(vector3.getX() - 1, vector3.getY() - 1, vector3.getZ() - 1), false).canPassThrough() ||
                        level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ() - 1), false).canPassThrough()) return true;
            } else if (xIsInt) {
                if (level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ()), false).canPassThrough() ||
                        level.getBlock(new Vector3(vector3.getX() - 1, vector3.getY() - 1, vector3.getZ()), false).canPassThrough()) return true;
            } else if (zIsInt) {
                if (level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ()), false).canPassThrough() ||
                        level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ() - 1), false).canPassThrough()) return true;
            } else {
                if (level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ()), false).canPassThrough()) return true;
            }
        }
        return false;
    }

    private ArrayList<Node> FloydSmooth(ArrayList<Node> array) {
        int current = 0;
        int total = 2;
        if (array.size() > 2) {
            while (total < array.size()) {
                if (!hasBarrier(array.get(current), array.get(total)) && total != array.size() - 1) {
                    total++;
                } else {
                    array.get(total - 1).setParent(array.get(current));
                    current = total - 1;
                    total++;
                }
            }

            Node temp = array.get(array.size() - 1);
            ArrayList<Node> tempL = new ArrayList<>();
            tempL.add(temp);
            while (temp.getParent() != null) {
                tempL.add((temp = temp.getParent()));
            }
            Collections.reverse(tempL);
            return tempL;
        }
        return array;
    }

    private ArrayList<Node> getPathRoute() {
        ArrayList<Node> nodes = new ArrayList<>();
        Node temp = closeList.get(closeList.size() - 1);
        nodes.add(temp);
        while (!temp.getParent().getVector3().equals(start)) {
            nodes.add(temp = temp.getParent());
        }
        nodes.add(temp.getParent());
        Collections.reverse(nodes);
        return nodes;

    }

    private boolean isPositionOverlap(Vector3 vector2, Vector3 vector2_) {
        return vector2.getFloorX() == vector2_.getFloorX()
                && vector2.getFloorZ() == vector2_.getFloorZ()
                && vector2.getFloorY() == vector2_.getFloorY();
    }

    public void resetTemporary() {
        this.openList.clear();
        this.closeList.clear();
        this.searchLimit = 100;
    }
}
