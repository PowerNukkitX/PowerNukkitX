package cn.nukkit.entity.ai.route.finder.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.entity.ai.route.data.Node;
import cn.nukkit.entity.ai.route.finder.SimpleRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.IPosEvaluator;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.VectorMath;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import cn.nukkit.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 标准A*寻路实现
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
@Setter
public class SimpleFlatAStarRouteFinder extends SimpleRouteFinder {

    //这些常量是为了避免开方运算而设置的
    //直接移动成本
    protected final static int DIRECT_MOVE_COST = 10;
    //倾斜移动成本
    protected final static int OBLIQUE_MOVE_COST = 14;

    protected final PriorityQueue<Node> openList = new PriorityQueue<>();

    protected final List<Node> closeList = new ArrayList<>();
    protected final HashSet<Vector3> closeHashSet = new HashSet<>();

    protected EntityIntelligent entity;

    protected Level level;

    protected Vector3 start;

    protected Vector3 target;

    protected Vector3 reachableTarget;

    protected boolean finished = false;
    protected boolean searching = false;

    protected boolean interrupt = false;

    protected boolean reachable = true;

    protected boolean enableFloydSmooth = true;

    //寻路最大深度
    protected int currentSearchDepth = 100;

    protected int maxSearchDepth = 100;

    public SimpleFlatAStarRouteFinder(IPosEvaluator blockEvaluator, EntityIntelligent entity) {
        super(blockEvaluator);
        this.entity = entity;
        this.level = entity.level;
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
        //若实体未处于active状态，则关闭路径平滑
        this.setEnableFloydSmooth(this.entity.isActive());
        //清空openList和closeList
        openList.clear();
        closeList.clear();
        closeHashSet.clear();
        //重置寻路深度
        currentSearchDepth = maxSearchDepth;

        //将起点放置到closeList中，以开始寻路
        //起点没有父节点，且我们不需要计算他的代价
        Node currentNode = new Node(start, null, 0, 0);
        var tmpNode = new Node(start, null, 0, 0);
        closeList.add(tmpNode);
        closeHashSet.add(tmpNode.getVector3());

        //若当前寻路点没有到达终点
        while (!isPositionOverlap(currentNode.getVector3(), target)) {
            //检查是否被中断了
            if (this.isInterrupt()) {
                currentSearchDepth = 0;
                this.searching = false;
                this.finished = true;
                this.reachable = false;
                return false;
            }
            //将当前节点周围的有效节点放入openList中
            putNeighborNodeIntoOpen(currentNode);
            //若未超出寻路深度，则获取代价最小的一个node并将其设置为currentNode
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

        //因为在前面是否到达终点的检查中我们只粗略检查了坐标的floor值
        //所以说这里我们还需要将其精确指向到终点
        Node targetNode = currentNode;
        if (!currentNode.getVector3().equals(target)) {
            targetNode = new Node(target, currentNode, 0, 0);
        }

        //如果无法到达，则取最接近终点的一个Node作为尾节点
        Node reachableNode = null;
        reachableTarget = currentReachable ? target : (reachableNode = getNearestNodeFromCloseList(target)).getVector3();
        List<Node> findingPath = currentReachable ? getPathRoute(targetNode) : getPathRoute(reachableNode);
        //使用floyd平滑路径
        if (enableFloydSmooth)
            findingPath = floydSmooth(findingPath);

        //清空上次的寻路结果
        this.resetNodes();
        //重置Node指针
        this.setNodeIndex(0);

        //写入结果
        this.addNode(findingPath);

        if (EntityAI.DEBUG) {
            findingPath.forEach(node -> {
                sendParticle("minecraft:balloon_gas_particle", node.getVector3(), Server.getInstance().getOnlinePlayers().values().toArray(Player.EMPTY_ARRAY));
            });
        }

        this.reachable = currentReachable;
        this.finished = true;
        this.searching = false;

        return true;
    }

    protected void sendParticle(String identifier, Vector3 pos, Player[] showPlayers) {
        SpawnParticleEffectPacket packet = new SpawnParticleEffectPacket();
        packet.identifier = identifier;
        packet.dimensionId = this.entity.level.getDimension();
        packet.position = pos.asVector3f();
        Arrays.stream(showPlayers).forEach(player -> {
            if (!player.isOnline())
                return;
            try {
                player.dataPacket(packet);
            } catch (Throwable ignore) {}
        });
    }

    /**
     * 获取指定位置的方块的移动Cost
     *
     * @param level
     * @param pos
     * @return cost
     */
    protected int getBlockMoveCostAt(@NotNull Level level, Vector3 pos) {
        return level.getTickCachedBlock(pos).getWalkThroughExtraCost() + level.getTickCachedBlock(pos.add(0, -1, 0)).getWalkThroughExtraCost();
    }

    /**
     * 将一个节点周围的有效节点放入OpenList中
     *
     * @param node 节点
     */
    protected void putNeighborNodeIntoOpen(@NotNull Node node) {
        boolean N, E, S, W;

        Vector3 vector3 = new Vector3(node.getVector3().getFloorX() + 0.5, node.getVector3().getY(), node.getVector3().getFloorZ() + 0.5);

        double offsetY;

        if ((offsetY = getAvailableHorizontalOffset(vector3)) != -384) {
            if (Math.abs(offsetY) > 0.25) {
                Vector3 vec = vector3.add(1, offsetY, 0);
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
                var cost = getBlockMoveCostAt(level, vec) + DIRECT_MOVE_COST + node.getG();
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
                var cost = getBlockMoveCostAt(level, vec) + DIRECT_MOVE_COST + node.getG();
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
                var cost = getBlockMoveCostAt(level, vec) + DIRECT_MOVE_COST + node.getG();
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
                var cost = getBlockMoveCostAt(level, vec) + DIRECT_MOVE_COST + node.getG();
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

        //我们不允许实体在上下坡的时候斜着走，因为这容易导致实体卡脚（原版也是这个逻辑）
        //接触水的时候就不需要这么判断了
        if (N && E && (((offsetY = getAvailableHorizontalOffset(vector3.add(1, 0, -1))) == 0) || (offsetY != -384 && entity.isTouchingWater()))) {
            Vector3 vec = vector3.add(1, offsetY, -1);
            if (!existInCloseList(vec)) {
                var cost = getBlockMoveCostAt(level, vec) + OBLIQUE_MOVE_COST + node.getG();
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
                var cost = getBlockMoveCostAt(level, vec) + OBLIQUE_MOVE_COST + node.getG();
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
                var cost = getBlockMoveCostAt(level, vec) + OBLIQUE_MOVE_COST + node.getG();
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
                var cost = getBlockMoveCostAt(level, vec) + OBLIQUE_MOVE_COST + node.getG();
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
     * 计算当前点到终点的代价H
     * 默认使用对角线+直线距离
     */
    protected int calH(Vector3 start, Vector3 target) {
        //使用DIRECT_MOVE_COST和OBLIQUE_MOVE_COST计算代价
        //计算对角线距离
        int obliqueCost = (int) (Math.abs(Math.min(target.x - start.x, target.z - start.z)) * OBLIQUE_MOVE_COST);
        //计算剩余直线距离
        int directCost = (int) ((Math.abs(Math.max(target.x - start.x, target.z - start.z)) - Math.abs(Math.min(target.x - start.x, target.z - start.z))) * DIRECT_MOVE_COST);
        return obliqueCost + directCost + (int) (Math.abs(target.y - start.y) * DIRECT_MOVE_COST);
    }

    /**
     * 获取目标坐标最高有效点（沿Y轴往下检查）
     */
    protected Block getHighestUnder(Vector3 vector3, int limit) {
        if (limit > 0) {
            for (int y = vector3.getFloorY(); y >= vector3.getFloorY() - limit; y--) {
                Block block = this.level.getTickCachedBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
                if (evalStandingBlock(block))
                    return block;
            }
            return null;
        }
        for (int y = vector3.getFloorY(); y >= -64; y--) {
            Block block = this.level.getTickCachedBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
            if (evalStandingBlock(block))
                return block;
        }
        return null;
    }

    /**
     * 指定位置是否可作为一个有效的节点
     */
    protected boolean evalPos(Vector3 pos) {
        return evalPos.evalPos(entity, pos);
    }

    /**
     * 指定方块上面是否可作为一个有效的节点
     */
    protected boolean evalStandingBlock(Block block) {
        return evalPos.evalStandingBlock(entity, block);
    }

    /**
     * @param vector3
     * @return 指定坐标可到达的最高点 (limit=4)
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
     * 指定两个Node之间是否有障碍物
     */
    protected boolean hasBarrier(Vector3 pos1, Vector3 pos2) {
        if (pos1.equals(pos2)) return false;
        return VectorMath.getPassByVector3(pos1, pos2).stream().anyMatch(
                (pos) -> {
                    var offsetX = pos.x - this.entity.x;
                    var offsetY = pos.y - this.entity.y;
                    var offsetZ = pos.z - this.entity.z;
                    var offsetBox = this.entity.getBoundingBox().getOffsetBoundingBox(offsetX, offsetY, offsetZ);
                    return Utils.hasCollisionBlocks(this.level, offsetBox);
                }
        );
    }

    /**
     * 使用Floyd算法平滑A*路径
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
            while (temp.getParent() != null) {
                tempL.add((temp = temp.getParent()));
            }
            Collections.reverse(tempL);
            return tempL;
        }
        return array;
    }

    /**
     * 将Node链转换成List<Node>样式的路径信息
     *
     * @param end 列表尾节点
     */
    protected List<Node> getPathRoute(@Nullable Node end) {
        List<Node> nodes = new ArrayList<>();
        if (end == null)
            end = closeList.get(closeList.size() - 1);
        nodes.add(end);
        if (end.getParent() != null) {
            while (!end.getParent().getVector3().equals(start)) {
                nodes.add(end = end.getParent());
            }
            nodes.add(end.getParent());
        } else {
            nodes.add(end);
        }
        Collections.reverse(nodes);
        return nodes;

    }

    /**
     * 获取接近指定坐标的最近的Node
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
     * 坐标是否重叠了 <br/>
     * 此方法只会比较坐标的floorX、floorY、floorZ
     */
    protected boolean isPositionOverlap(Vector3 vector2, Vector3 vector2_) {
        return vector2.getFloorX() == vector2_.getFloorX()
                && vector2.getFloorZ() == vector2_.getFloorZ()
                && vector2.getFloorY() == vector2_.getFloorY();
    }
}
