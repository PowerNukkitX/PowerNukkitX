package cn.nukkit.entity.path;

/**
 * A*是一种较慢但远比mc原版快的寻路算法，此实现有待改进
 */
public class AStarPathFinder {
    public static final int DEFAULT_MAX_RECURSION = 200;

    protected NodeStore closeNodes;
    protected SortedNodeStore openNodes;

    public Node destination;
    public Node start;
    public PathThinker pathThinker;

    private SearchShape searchShape;

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
                    return destination.parent != null;
                }
                if (minCostNode.equals(destination)) {
                    destination.setParent(minCostNode);
                    return true;
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
                        closeNodes.add(each);
                        continue;
                    }
                    var newG = minCostNode.g + cost;
                    var oldNode = openNodes.get(each.nodeHashCode());
                    if (oldNode != null && oldNode.g > newG) {
                        oldNode.g = newG;
                        oldNode.parent = minCostNode;
                    } else {
                        each.g = newG;
                        each.parent = minCostNode;
                        openNodes.add(each);
                    }
                }
                closeNodes.add(minCostNode);
            }
        }
        return false;
    }

    public void reset() {
        if (closeNodes != null) {
            closeNodes.clear();
        } else {
            closeNodes = new RandomNodeStoreImpl();
        }
        if (openNodes != null) {
            openNodes.clear();
        } else {
            openNodes = new SortedNodeStoreImpl();
        }
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
}
