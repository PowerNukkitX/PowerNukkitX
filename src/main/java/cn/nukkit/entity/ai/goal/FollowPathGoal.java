package cn.nukkit.entity.ai.goal;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.path.Node;
import cn.nukkit.entity.ai.path.PathFinder;
import cn.nukkit.math.NukkitMath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class FollowPathGoal extends AbstractGoal {
    public static final String ID = "minecraft:follow_path";

    @NotNull
    protected Deque<Node> nodeStack = new LinkedList<>();
    @Nullable
    protected PathFinder currentPathFinder = null;
    protected int lastUpdateNodeTick = -1;

    @Override
    public boolean shouldStart(int currentTick, EntityIntelligent entity) {
        return entity.getPathFinder() != null && super.shouldStart(currentTick, entity);
    }

    @Override
    public void start(int currentTick, EntityIntelligent entity) {
        super.start(currentTick, entity);
        var pathFinder = entity.getPathFinder();
        if (pathFinder == null) {
            goalState = GoalState.NOT_WORKING;
            return;
        }
        var current = pathFinder.getDestination();
        while (current != null) {
            nodeStack.addFirst(current);
            current = current.getParent();
        }
        nodeStack.pop();
        currentPathFinder = pathFinder;
    }
//
//    /**
//     * 对最后部分进行路径平滑，实际上我们只平滑了实体到玩家最后一段路的距离，这段路玩家感受最明显
//     */
//    protected void floyd(EntityIntelligent entity) {
//        // 先取出后16个点
//        var nodeList = new ArrayList<Node>(16);
//        for (int i = 0; i < 16 && !nodeStack.isEmpty(); i++) {
//            nodeList.add(nodeStack.removeLast());
//        }
//        // 去掉非跳点（路径上的拐点）
//        for (int i = 1; i < nodeList.size() - 2; i++) {
//            var n0 = nodeList.get(i - 1);
//            var n1 = nodeList.get(i);
//            var n2 = nodeList.get(i + 1);
//            if (n2.doubleRealX() - n1.doubleRealX() == n1.doubleRealX() - n0.doubleRealX() &&
//                    n2.doubleRealZ() - n1.doubleRealZ() == n1.doubleRealZ() - n0.doubleRealZ() &&
//                    n2.doubleRealY() - n1.doubleRealY() == n1.doubleRealY() - n0.doubleRealY()) {
//                nodeList.remove(i);
//                i--;
//            }
//        }
//        // 把剩下的点连线检查能否直接走过
//        for (int i = 1; i < nodeList.size() - 2; i++) {
//            var n0 = nodeList.get(i - 1);
//            var n2 = nodeList.get(i + 1);
//            if (canDirectlyPassThrough(entity, n0, n2)) {
//                nodeList.remove(i);
//                i--;
//            }
//        }
//        nodeStack.addAll(nodeList);
//    }
//
//    /**
//     * 检查两点间是否可以直接直线通过
//     *
//     * @param n1 起点
//     * @param n2 终点
//     */
//    protected final boolean canDirectlyPassThrough(EntityIntelligent entity, Node n1, Node n2) {
//        var rx = n1.realX();
//        var ry = n1.realY();
//        var rz = n1.realZ();
//        var dx = n2.realX() - rx;
//        var dz = n2.realZ() - rz;
//        double px;
//        double pz;
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
//        rx -= px;
//        rz -= pz;
//        for (int i = 0; i < cnt; i++) {
//            rx += px;
//            rz += pz;
//            if (!entity.canDirectlyPassThrough(rx, ry, rz)) {
//                return false;
//            }
//        }
//        return true;
//    }

    @Override
    public boolean shouldContinue(int currentTick, EntityIntelligent entity) {
        return !nodeStack.isEmpty() && super.shouldContinue(currentTick, entity);
    }

    protected int getFailToMoveTick() {
        return 60;
    }

    @Override
    public void execute(int currentTick, EntityIntelligent entity) {
        if (entity.getPathFinder() != currentPathFinder) {
            nodeStack.clear();
            start(currentTick, entity);
        }
        if (entity.movingNearDestination == null) {
            if (nodeStack.isEmpty()) {
                return;
            }
            var tmp = nodeStack.pop();
            lastUpdateNodeTick = currentTick;
            entity.movingNearDestination = tmp.toRealVector();
        }
    }

    @Override
    public boolean shouldStop(int currentTick, EntityIntelligent entity) {
        return nodeStack.isEmpty() || entity.getPathFinder() == null || currentTick - lastUpdateNodeTick > getFailToMoveTick();
    }

    @Override
    public void stop(int currentTick, EntityIntelligent entity) {
        super.stop(currentTick, entity);
        nodeStack.clear();
        entity.setPathFinder(null);
        currentPathFinder = null;
    }

    @Override
    public int getOrder() {
        return ORDER_FOLLOW_PATH;
    }

    @Override
    public String getId() {
        return ID;
    }
}
