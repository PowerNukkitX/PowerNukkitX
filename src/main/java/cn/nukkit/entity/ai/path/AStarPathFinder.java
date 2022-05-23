package cn.nukkit.entity.ai.path;

import cn.nukkit.math.Vector3;

/**
 * A*是一种较慢但远比mc原版快的寻路算法，此实现有待改进
 */
public class AStarPathFinder implements PathFinderDynamic {
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
        var result = search0();
        if (result) {
            return true;
        } else {
            result = search1();
        }
        if (!result) {
            destination.setParent(null);
        }
        return result;
    }

    /**
     * 如果能直接走过去就没必要A*搜索了
     *
     * @return 能否直接走过去
     */
    protected boolean search0() {
        Node tmpDes = destination;
        var rdx = destination.doubleRealX() - start.doubleRealX();
        var rdy = destination.doubleRealY() - start.doubleRealY();
        var rdz = destination.doubleRealZ() - start.doubleRealZ();
        var sLen = (long) rdx * rdx + (long) rdy * rdy + (long) rdz * rdz;
        if (sLen > 8 || Math.abs(rdy) > 2) {
            return false;
        }
        if (allowedOffsetSquared != 0) {
            if (sLen > allowedOffsetSquared) {
                var angle = Math.atan2(rdz, rdx);
                var len = Math.sqrt(allowedOffsetSquared * 0.25);
                var dx = len * Math.cos(angle);
                var dz = len * Math.sin(angle);
                tmpDes = new Node(destination.realX() - dx, destination.realY(), destination.realZ() - dz, destination);
            } else {
                destination = start;
                return true;
            }
        }
        var rx = start.realX();
        var ry = start.realY();
        var rz = start.realZ();
        var dx = tmpDes.realX() - rx;
        var dz = tmpDes.realZ() - rz;
        double px;
        double pz;
        int cnt;
        if (Math.abs(dx) > Math.abs(dz)) {
            px = 1;
            cnt = (int) Math.abs(dx);
            pz = dz / cnt;
        } else {
            pz = 1;
            cnt = (int) Math.abs(dz);
            px = dx / cnt;
        }
        rx -= px;
        rz -= pz;
        for (int i = 0; i < cnt; i++) {
            rx += px;
            rz += pz;
            if (!pathThinker.canDirectlyPassThrough(rx, ry, rz)) {
                return false;
            }
        }
        destination = tmpDes;
        tmpDes.setParent(start);
        return true;
    }

    protected boolean search1() {
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
                        setDestination(minCostNode);
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

    public long getAllowedOffsetSquared() {
        return allowedOffsetSquared;
    }
}
