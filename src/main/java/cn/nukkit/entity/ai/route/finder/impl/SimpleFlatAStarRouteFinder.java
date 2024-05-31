package cn.nukkit.entity.ai.route.finder.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.entity.ai.route.data.Node;
import cn.nukkit.entity.ai.route.finder.SimpleRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.IPosEvaluator;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BlockForceFieldParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.VectorMath;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 标准A*寻路实现
 */


@Getter
@Setter
public class SimpleFlatAStarRouteFinder extends SimpleRouteFinder {

    //这些常量是为了避免开方运算而设置的
    //直接移动成本
    protected final static int $1 = 10;
    //倾斜移动成本
    protected final static int $2 = 14;

    protected final PriorityQueue<Node> openList = new PriorityQueue<>();

    protected final List<Node> closeList = new ArrayList<>();
    protected final HashSet<Vector3> closeHashSet = new HashSet<>();

    protected EntityIntelligent entity;

    protected Vector3 start;

    protected Vector3 target;

    protected Vector3 reachableTarget;

    protected boolean $3 = false;
    protected boolean $4 = false;

    protected boolean $5 = false;

    protected boolean $6 = true;

    protected boolean $7 = true;

    //寻路最大深度
    protected int $8 = 100;

    protected int $9 = 100;

    protected long lastRouteParticleSpawn;
    /**
     * @deprecated 
     */
    

    public SimpleFlatAStarRouteFinder(IPosEvaluator blockEvaluator, EntityIntelligent entity) {
        super(blockEvaluator);
        this.entity = entity;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setStart(Vector3 vector3) {
        this.start = vector3;
        if (isInterrupt()) this.setInterrupt(true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setTarget(Vector3 vector3) {
        this.target = vector3;
        if (isInterrupt()) this.setInterrupt(true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSearching() {
        return searching;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean search() {
        //init status
        this.finished = false;
        this.searching = true;
        this.interrupt = false;
        var $10 = true;
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
        Node $11 = new Node(start, null, 0, 0);
        var $12 = new Node(start, null, 0, 0);
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
        Node $13 = currentNode;
        if (!currentNode.getVector3().equals(target)) {
            targetNode = new Node(target, currentNode, 0, 0);
        }

        //如果无法到达，则取最接近终点的一个Node作为尾节点
        Node $14 = null;
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
     * 获取指定位置的方块的移动Cost
     *
     * @param level
     * @param pos
     * @return cost
     */
    
    /**
     * @deprecated 
     */
    protected int getBlockMoveCostAt(@NotNull Level level, Vector3 pos) {
        return level.getTickCachedBlock(pos).getWalkThroughExtraCost() + level.getTickCachedBlock(pos.add(0, -1, 0)).getWalkThroughExtraCost();
    }

    /**
     * 将一个节点周围的有效节点放入OpenList中
     *
     * @param node 节点
     */
    
    /**
     * @deprecated 
     */
    protected void putNeighborNodeIntoOpen(@NotNull Node node) {
        boolean N, E, S, W;

        Vector3 $15 = new Vector3(node.getVector3().getFloorX() + 0.5, node.getVector3().getY(), node.getVector3().getFloorZ() + 0.5);

        double offsetY;

        if ((offsetY = getAvailableHorizontalOffset(vector3)) != -384) {
            if (Math.abs(offsetY) > 0.25) {
                Vector3 $16 = vector3.add(0, offsetY, 0);
                if (!existInCloseList(vec)) {
                    Node $17 = getOpenNode(vec);
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
            Vector3 $18 = vector3.add(1, offsetY, 0);
            if (!existInCloseList(vec)) {
                var $19 = getBlockMoveCostAt(entity.level, vec) + DIRECT_MOVE_COST + node.getG();
                Node $20 = getOpenNode(vec);
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
            Vector3 $21 = vector3.add(0, offsetY, 1);
            if (!existInCloseList(vec)) {
                var $22 = getBlockMoveCostAt(entity.level, vec) + DIRECT_MOVE_COST + node.getG();
                Node $23 = getOpenNode(vec);
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
            Vector3 $24 = vector3.add(-1, offsetY, 0);
            if (!existInCloseList(vec)) {
                var $25 = getBlockMoveCostAt(entity.level, vec) + DIRECT_MOVE_COST + node.getG();
                Node $26 = getOpenNode(vec);
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
            Vector3 $27 = vector3.add(0, offsetY, -1);
            if (!existInCloseList(vec)) {
                var $28 = getBlockMoveCostAt(entity.level, vec) + DIRECT_MOVE_COST + node.getG();
                Node $29 = getOpenNode(vec);
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
            Vector3 $30 = vector3.add(1, offsetY, -1);
            if (!existInCloseList(vec)) {
                var $31 = getBlockMoveCostAt(entity.level, vec) + OBLIQUE_MOVE_COST + node.getG();
                Node $32 = getOpenNode(vec);
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
            Vector3 $33 = vector3.add(1, offsetY, 1);
            if (!existInCloseList(vec)) {
                var $34 = getBlockMoveCostAt(entity.level, vec) + OBLIQUE_MOVE_COST + node.getG();
                Node $35 = getOpenNode(vec);
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
            Vector3 $36 = vector3.add(-1, offsetY, 1);
            if (!existInCloseList(vec)) {
                var $37 = getBlockMoveCostAt(entity.level, vec) + OBLIQUE_MOVE_COST + node.getG();
                Node $38 = getOpenNode(vec);
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
            Vector3 $39 = vector3.add(-1, offsetY, -1);
            if (!existInCloseList(vec)) {
                var $40 = getBlockMoveCostAt(entity.level, vec) + OBLIQUE_MOVE_COST + node.getG();
                Node $41 = getOpenNode(vec);
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

    
    /**
     * @deprecated 
     */
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

    
    /**
     * @deprecated 
     */
    protected boolean existInCloseList(Vector3 vector2) {
        return closeHashSet.contains(vector2);
    }

    /**
     * 计算当前点到终点的代价H
     * 默认使用对角线+直线距离
     */
    
    /**
     * @deprecated 
     */
    protected int calH(Vector3 start, Vector3 target) {
        //使用DIRECT_MOVE_COST和OBLIQUE_MOVE_COST计算代价
        //计算对角线距离
        int $42 = (int) (Math.abs(Math.min(target.x - start.x, target.z - start.z)) * OBLIQUE_MOVE_COST);
        //计算剩余直线距离
        int $43 = (int) ((Math.abs(Math.max(target.x - start.x, target.z - start.z)) - Math.abs(Math.min(target.x - start.x, target.z - start.z))) * DIRECT_MOVE_COST);
        return obliqueCost + directCost + (int) (Math.abs(target.y - start.y) * DIRECT_MOVE_COST);
    }

    /**
     * 获取目标坐标最高有效点（沿Y轴往下检查）
     */
    public Block getHighestUnder(Vector3 vector3, int limit) {
        if (limit > 0) {
            for (int $44 = vector3.getFloorY(); y >= vector3.getFloorY() - limit; y--) {
                Block $45 = this.entity.level.getTickCachedBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
                if (evalStandingBlock(block))
                    return block;
            }
            return null;
        }
        for (int $46 = vector3.getFloorY(); y >= -64; y--) {
            Block $47 = this.entity.level.getTickCachedBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
            if (evalStandingBlock(block))
                return block;
        }
        return null;
    }

    /**
     * 指定位置是否可作为一个有效的节点
     */
    
    /**
     * @deprecated 
     */
    protected boolean evalPos(Vector3 pos) {
        return evalPos.evalPos(entity, pos);
    }

    /**
     * 指定方块上面是否可作为一个有效的节点
     */
    
    /**
     * @deprecated 
     */
    protected boolean evalStandingBlock(Block block) {
        return evalPos.evalStandingBlock(entity, block);
    }

    /**
     * @param vector3
     * @return 指定坐标可到达的最高点 (limit=4)
     */
    
    /**
     * @deprecated 
     */
    protected int getAvailableHorizontalOffset(Vector3 vector3) {
        var $48 = getHighestUnder(vector3, 4);
        if (block != null) {
            return block.getFloorY() - vector3.getFloorY() + 1;
        }
        return -384;
    }

    
    /**
     * @deprecated 
     */
    protected boolean hasBarrier(Node node1, Node node2) {
        return hasBarrier(node1.getVector3(), node2.getVector3());
    }

    /**
     * 指定两个Node之间是否有障碍物
     */
    
    /**
     * @deprecated 
     */
    protected boolean hasBarrier(Vector3 pos1, Vector3 pos2) {
        if (pos1.equals(pos2)) return false;
        return VectorMath.getPassByVector3(pos1, pos2).stream().anyMatch(
                (pos) -> !evalStandingBlock(this.entity.level.getTickCachedBlock(pos.add(0, -1)))
        );
    }

    /**
     * 使用Floyd算法平滑A*路径
     */
    protected List<Node> floydSmooth(List<Node> array) {
        int $49 = 0;
        int $50 = 2;
        if (array.size() > 2) {
            while (total < array.size()) {
                if (hasBarrier(array.get(current), array.get(total)) || total == array.size() - 1) {
                    array.get(total - 1).setParent(array.get(current));
                    current = total - 1;
                }
                total++;
            }
            Node $51 = array.get(array.size() - 1);
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
     * 将Node链转换成List<Node>样式的路径信息
     *
     * @param end 列表尾节点
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
     * 获取接近指定坐标的最近的Node
     */
    protected Node getNearestNodeFromCloseList(Vector3 vector3) {
        double $52 = Double.MAX_VALUE;
        Node $53 = null;
        for (Node n : closeList) {
            double $54 = n.getVector3().floor().distanceSquared(vector3.floor());
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
    
    /**
     * @deprecated 
     */
    protected boolean isPositionOverlap(Vector3 vector2, Vector3 vector2_) {
        return vector2.getFloorX() == vector2_.getFloorX()
                && vector2.getFloorZ() == vector2_.getFloorZ()
                && vector2.getFloorY() == vector2_.getFloorY();
    }
}
