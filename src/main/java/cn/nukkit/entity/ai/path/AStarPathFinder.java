package cn.nukkit.entity.ai.path;

import cn.nukkit.math.NukkitMath;

/**
 * A*是一种较慢但远比mc原版快的寻路算法，此实现有待改进
 */
public class AStarPathFinder {
    public static final int DEFAULT_MAX_RECURSION = 512;

    protected NodeStore closeNodes;
    protected SortedNodeStore openNodes;

    public Node destination;
    public Node start;
    public PathThinker pathThinker;

    private SearchShape searchShape;
    private long allowedOffsetSquared = 0;

    /**
     * 递归搜索深度限制
     */
    protected int limit = DEFAULT_MAX_RECURSION;

    public AStarPathFinder() {
        this.closeNodes = new RandomNodeStoreImpl();
        this.openNodes = new SortedNodeStoreImpl();
    }

    public void prepareSearch() {
        reset();
        openNodes.add(start);
        searchShape = pathThinker.getSearchShape();
    }

    public boolean search() {
        if (destination == null || start == null || pathThinker == null) {
            return false;
        }
        for (int i = 0; i < limit; i++) {
            if (openNodes.size() == 0) {
                return false;
            } else {
                var minCostNode = openNodes.popMinCost();
                if (minCostNode == null) {
                    return destination.getParent() != null;
                }
                if (allowedOffsetSquared == 0 && minCostNode.equals(destination)) {
                    destination.setParent(minCostNode.getParent());
                    destination.setG(minCostNode.getG());
                    return true;
                } else {
                    var dx = destination.doubleRealX() - minCostNode.doubleRealX();
                    var dy = destination.doubleRealY() - minCostNode.doubleRealY();
                    var dz = destination.doubleRealZ() - minCostNode.doubleRealZ();
                    if ((long) dx * dx + (long) dy * dy + (long) dz * dz < allowedOffsetSquared) {
                        destination.setParent(minCostNode.getParent());
                        destination.setG(minCostNode.getG());
                        return true;
                    }
                }
                searchShape.setCenterNode(minCostNode);
                for (var each : searchShape) {
                    if (closeNodes.has(each)) {
                        continue;
                    }
                    if (!pathThinker.canPassThrough(each)) {
                        closeNodes.add(each);
                        continue;
                    }
                    var cost = pathThinker.calcCost(minCostNode, each);
                    if (cost == Long.MAX_VALUE) {
                        continue;
                    }
                    var newG = minCostNode.getG() + cost;
                    var oldNode = openNodes.remove(each.nodeHashCode());
                    if (oldNode != null) {
                        if (oldNode.getG() > newG) {
                            each.setG(newG);
                            each.setParent(minCostNode);
                            openNodes.add(oldNode);
                        } else {
                            openNodes.add(oldNode);
                        }
                    } else {
                        each.setG(newG);
                        each.setParent(minCostNode);
                        openNodes.add(each);
                    }
                }
                closeNodes.add(minCostNode);
            }
        }
        return false;
    }

    public void reset() {
        closeNodes = new RandomNodeStoreImpl();
        openNodes = new SortedNodeStoreImpl();
    }

    public NodeStore getCloseNodes() {
        return closeNodes;
    }

    public void setCloseNodes(NodeStore closeNodes) {
        this.closeNodes = closeNodes;
    }

    public SortedNodeStore getOpenNodes() {
        return openNodes;
    }

    public void setOpenNodes(SortedNodeStore openNodes) {
        this.openNodes = openNodes;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public Node getStart() {
        return start;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public PathThinker getPathThinker() {
        return pathThinker;
    }

    public void setPathThinker(PathThinker pathThinker) {
        this.pathThinker = pathThinker;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void allowDestinationOffset(int doubleOffset) {
        this.allowedOffsetSquared = (long) doubleOffset * doubleOffset;
    }

    public void allowDestinationOffset(float offset) {
        allowDestinationOffset(NukkitMath.floorFloat(offset * 2));
    }

    public long getAllowedOffsetSquared() {
        return allowedOffsetSquared;
    }
}
