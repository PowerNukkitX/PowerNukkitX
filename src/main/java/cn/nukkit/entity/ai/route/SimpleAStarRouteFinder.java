package cn.nukkit.entity.ai.route;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.blockevaluator.IBlockEvaluator;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import cn.nukkit.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
@Setter
public class SimpleAStarRouteFinder extends SimpleRouteFinder {

    //这些常量是为了避免开方运算而设置的
    //直接移动成本
    protected final static int DIRECT_MOVE_COST = 10;
    //倾斜移动成本
    protected final static int OBLIQUE_MOVE_COST = 14;

    protected final PriorityQueue<Node> openList = new PriorityQueue<>();

    protected final ArrayList<Node> closeList = new ArrayList<>();

    protected EntityIntelligent entity;

    protected Level level;

    protected Vector3 start;

    protected Vector3 target;

    protected boolean finished = false;
    protected boolean searching = false;

    protected boolean interrupt = false;

    protected boolean reachable = true;

    protected boolean enableFloydSmooth = true;

    //寻路最大深度
    protected int currentSearchDepth = 100;

    protected int maxSearchDepth = 100;

    public SimpleAStarRouteFinder(IBlockEvaluator blockEvaluator, EntityIntelligent entity) {
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
        //清空openList和closeList
        openList.clear();
        closeList.clear();
        //重置寻路深度
        currentSearchDepth = maxSearchDepth;

        //将起点放置到closeList中，以开始寻路
        //起点没有父节点，且我们不需要计算他的代价
        Node currentNode = new Node(start, null, 0, 0);
        closeList.add(new Node(start, null, 0, 0));

        //若当前寻路点没有到达终点
        while (!isPositionOverlap(currentNode.getVector3(), target)) {
            //检查是否被中断了
            if (this.isInterrupt()) {
                currentSearchDepth = 0;
                this.searching = false;
                this.finished = true;
                return false;
            }
            //将当前节点周围的有效节点放入openList中
            putNeighborNodeIntoOpen(currentNode);
            //若未超出寻路深度，则获取代价最小的一个node并将其设置为currentNode
            if (openList.peek() != null && currentSearchDepth-- > 0) {
                closeList.add(currentNode = openList.poll());
            } else {
                this.searching = false;
                this.finished = true;
                this.reachable = false;
                break;
            }
        }

        //因为在前面是否到达终点的检查中我们只粗略检查了坐标的floor值
        //所以说这里我们还需要将其精确指向到终点
        Node targetNode = null;
        if (!currentNode.getVector3().equals(target)) {
            targetNode = new Node(target, currentNode, 0, 0);
        }

        //如果无法到达，则取最接近终点的一个Node作为尾节点
        ArrayList<Node> findingPath = this.reachable ? getPathRoute(targetNode) : getPathRoute(getNearestNodeFromCloseList(target));
        //使用floyd平滑路径
        if (enableFloydSmooth)
            findingPath = FloydSmooth(findingPath);

        //清空上次的寻路结果
        this.resetNodes();
        //重置Node指针
        this.setNodeIndex(0);

        //写入结果
        this.addNode(findingPath);

        //debug
        findingPath.forEach(node -> {
            sendParticle("minecraft:balloon_gas_particle",node.getVector3(), Server.getInstance().getOnlinePlayers().values().toArray(Player.EMPTY_ARRAY));
        });

        this.finished = true;
        this.searching = false;
        this.reachable = true;

        return true;
    }

    //debug
    private void sendParticle(String identifier, Vector3 pos, Player[] showPlayers) {
        Arrays.stream(showPlayers).forEach(player -> {
            if (!player.isOnline())
                return;
            SpawnParticleEffectPacket packet = new SpawnParticleEffectPacket();
            packet.identifier = identifier;
            packet.dimensionId = this.entity.level.getDimension();
            packet.position = pos.asVector3f();
            try {
                player.dataPacket(packet);
            }catch (Throwable t){}
        });
    }

    /**
     * 将一个节点周围的有效节点放入OpenList中
     *
     * @param node
     */
    protected void putNeighborNodeIntoOpen(Node node) {
        boolean N, E, S, W;

        Vector3 vector3 = new Vector3(node.getVector3().getFloorX() + 0.5, node.getVector3().getY(), node.getVector3().getFloorZ() + 0.5);

        double y;

        if (E = ((y = getAvailableHorizontalOffset(vector3.add(1, 0, 0))) != -384)) {
            Vector3 vec = vector3.add(1, y, 0);
            if (!existInCloseList(vec)) {
                Node nodeNear = getOpenNode(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, DIRECT_MOVE_COST + node.getG(), calH(vec, target)));
                } else {
                    if (node.getG() + DIRECT_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (S = ((y = getAvailableHorizontalOffset(vector3.add(0, 0, 1))) != -384)) {
            Vector3 vec = vector3.add(0, y, 1);
            if (!existInCloseList(vec)) {
                Node nodeNear = getOpenNode(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, DIRECT_MOVE_COST + node.getG(), calH(vec, target)));
                } else {
                    if (node.getG() + DIRECT_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (W = ((y = getAvailableHorizontalOffset(vector3.add(-1, 0, 0))) != -384)) {
            Vector3 vec = vector3.add(-1, y, 0);
            if (!existInCloseList(vec)) {
                Node nodeNear = getOpenNode(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, DIRECT_MOVE_COST + node.getG(), calH(vec, target)));
                } else {
                    if (node.getG() + DIRECT_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (N = ((y = getAvailableHorizontalOffset(vector3.add(0, 0, -1))) != -384)) {
            Vector3 vec = vector3.add(0, y, -1);
            if (!existInCloseList(vec)) {
                Node nodeNear = getOpenNode(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, DIRECT_MOVE_COST + node.getG(), calH(vec, target)));
                } else {
                    if (node.getG() + DIRECT_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + DIRECT_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        //我们不允许实体在上下坡的时候斜着走，因为这容易导致实体卡脚（原版也是这个逻辑）
        //接触水的时候就不需要这么判断了
        if (N && E && (((y = getAvailableHorizontalOffset(vector3.add(1, 0, -1))) == 0) || entity.isTouchingWater())) {
            Vector3 vec = vector3.add(1, y, -1);
            if (!existInCloseList(vec)) {
                Node nodeNear = getOpenNode(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, OBLIQUE_MOVE_COST + node.getG(), calH(vec, target)));
                } else {
                    if (node.getG() + OBLIQUE_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (E && S && (((y = getAvailableHorizontalOffset(vector3.add(1, 0, 1))) == 0) || entity.isTouchingWater())) {
            Vector3 vec = vector3.add(1, y, 1);
            if (!existInCloseList(vec)) {
                Node nodeNear = getOpenNode(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, OBLIQUE_MOVE_COST + node.getG(), calH(vec, target)));
                } else {
                    if (node.getG() + OBLIQUE_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (W && S && (((y = getAvailableHorizontalOffset(vector3.add(-1, 0, 1))) == 0) || entity.isTouchingWater())) {
            Vector3 vec = vector3.add(-1, y, 1);
            if (!existInCloseList(vec)) {
                Node nodeNear = getOpenNode(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, OBLIQUE_MOVE_COST + node.getG(), calH(vec, target)));
                } else {
                    if (node.getG() + OBLIQUE_MOVE_COST < nodeNear.getG()) {
                        nodeNear.setParent(node);
                        nodeNear.setG(node.getG() + OBLIQUE_MOVE_COST);
                        nodeNear.setF(nodeNear.getG() + nodeNear.getH());
                    }
                }
            }
        }

        if (W && N && (((y = getAvailableHorizontalOffset(vector3.add(-1, 0, -1))) == 0) || entity.isTouchingWater())) {
            Vector3 vec = vector3.add(-1, y, -1);
            if (!existInCloseList(vec)) {
                Node nodeNear = getOpenNode(vec);
                if (nodeNear == null) {
                    this.openList.offer(new Node(vec, node, OBLIQUE_MOVE_COST + node.getG(), calH(vec, target)));
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
        return getCloseNode(vector2) != null;
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
                Block block = this.level.getBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
                if ((evalBlock(block) != -1)/* && level.getBlock(block.add(0, 1, 0), false).getId() == Block.AIR*/)
                    return block;
            }
            return null;
        }
        for (int y = vector3.getFloorY(); y >= -64; y--) {
            Block block = this.level.getBlock(vector3.getFloorX(), y, vector3.getFloorZ(), false);
            if ((evalBlock(block) != -1)/* && level.getBlock(block.add(0, 1, 0), false).getId() == Block.AIR*/)
                return block;
        }
        return null;
    }

    /**
     * 指定坐标是否有效
     */
    //todo: 实现方块权重
    protected int evalBlock(Block block) {
        return blockEvaluator.evalBlock(entity,block);
    }

    /**
     * @param vector3
     * @return 获取指定坐标可到达的最高点 (limit=4)
     */
    protected int getAvailableHorizontalOffset(Vector3 vector3) {
        Block block = getHighestUnder(vector3, 4);
        if (block != null) {
            return (block.getFloorY() - vector3.getFloorY()) + 1;
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

    protected boolean hasBlocksAround(ArrayList<Vector3> list) {
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
                        level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ() - 1), false).canPassThrough())
                    return true;
            } else if (xIsInt) {
                if (level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ()), false).canPassThrough() ||
                        level.getBlock(new Vector3(vector3.getX() - 1, vector3.getY() - 1, vector3.getZ()), false).canPassThrough())
                    return true;
            } else if (zIsInt) {
                if (level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ()), false).canPassThrough() ||
                        level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ() - 1), false).canPassThrough())
                    return true;
            } else {
                if (level.getBlock(new Vector3(vector3.getX(), vector3.getY() - 1, vector3.getZ()), false).canPassThrough())
                    return true;
            }
        }
        return false;
    }

    /**
     * 使用Floyd算法平滑A*路径
     */
    protected ArrayList<Node> FloydSmooth(ArrayList<Node> array) {
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

    /**
     * 将Node链转换成List<Node>样式的路径信息
     *
     * @param end 链表尾节点
     */
    protected ArrayList<Node> getPathRoute(@Nullable Node end) {
        ArrayList<Node> nodes = new ArrayList<>();
        if (end == null)
            end = closeList.get(closeList.size() - 1);
        nodes.add(end);
        if (end.getParent() != null) {
            while (!end.getParent().getVector3().equals(start)) {
                nodes.add(end = end.getParent());
            }
            nodes.add(end.getParent());
        }else{
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
     * 坐标是否重叠了
     * 此方法只会比较坐标的floorX、floorY、floorZ
     */
    protected boolean isPositionOverlap(Vector3 vector2, Vector3 vector2_) {
        return vector2.getFloorX() == vector2_.getFloorX()
                && vector2.getFloorZ() == vector2_.getFloorZ()
                && vector2.getFloorY() == vector2_.getFloorY();
    }
}
