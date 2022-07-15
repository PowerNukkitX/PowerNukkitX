package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.route.blockevaluator.IBlockEvaluator;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * 非异步的路径查找抽象类
 * 在PowerNukkitX的生物AI架构中，不同实体的路径查找是并行的而不是异步的
 * 所以说我们并不需要异步路径查找
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public abstract class SimpleRouteFinder implements IRouteFinder{

    //用于存储寻路结果的List
    protected ArrayList<Node> nodes = new ArrayList<>();

    //Node索引
    protected int currentIndex = 0;

    //方块评估器
    protected IBlockEvaluator blockEvaluator;

    public SimpleRouteFinder(IBlockEvaluator blockEvaluator) {
        this.blockEvaluator = blockEvaluator;
    }

    /**
     * 添加寻路结果节点
     */
    protected void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * 批量添加寻路结果节点
     */
    protected void addNode(ArrayList<Node> node) {
        nodes.addAll(node);
    }

    /**
     * 重置结果
     */
    protected void resetNodes() {
        this.nodes.clear();
    }

    /**
     * 线程安全地获取查找到的路径信息（cloned）
     */
    @Override
    public ArrayList<Node> getRoute() {
        return new ArrayList<>(this.nodes);
    }

    @Override
    @Nullable
    public Node getCurrentNode() {
        if (this.hasCurrentNode()) {
            return nodes.get(currentIndex);
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        try {
            if (this.currentIndex + 1 < nodes.size()) {
                return this.nodes.get(this.currentIndex + 1) != null;
            }
        } catch (Exception ignore) {}
        return false;
    }

    @Override
    @Nullable
    public Node next() {
        if (this.hasNext()) {
            return this.nodes.get(++currentIndex);
        }
        return null;
    }

    @Override
    public boolean hasCurrentNode() {
        return currentIndex < this.nodes.size();
    }

    @Override
    public void setNodeIndex(int index) {
        this.currentIndex = index;
    }

    @Override
    public int getNodeIndex() {
        return this.currentIndex;
    }

    @Nullable
    @Override
    public Node getNode(int index){
        if (index + 1 < nodes.size()) {
            return this.nodes.get(index);
        }
        return null;
    }
}
