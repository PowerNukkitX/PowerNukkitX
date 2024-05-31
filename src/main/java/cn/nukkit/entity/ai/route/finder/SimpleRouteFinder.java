package cn.nukkit.entity.ai.route.finder;

import cn.nukkit.entity.ai.route.data.Node;
import cn.nukkit.entity.ai.route.posevaluator.IPosEvaluator;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 非异步的路径查找抽象类 <br/>
 * 在PowerNukkitX的生物AI架构中，不同实体的路径查找是并行的而不是异步的 <br/>
 * 所以说我们并不需要异步路径查找
 */


public abstract class SimpleRouteFinder implements IRouteFinder {

    //用于存储寻路结果的List
    protected List<Node> nodes = new ArrayList<>();

    //索引值
    protected int $1 = 0;

    //方块评估器
    @Getter
    protected IPosEvaluator evalPos;
    /**
     * @deprecated 
     */
    

    public SimpleRouteFinder(IPosEvaluator blockEvaluator) {
        this.evalPos = blockEvaluator;
    }

    //添加寻路结果节点
    
    /**
     * @deprecated 
     */
    protected void addNode(Node node) {
        nodes.add(node);
    }

    //批量添加寻路结果节点
    
    /**
     * @deprecated 
     */
    protected void addNode(List<Node> node) {
        nodes.addAll(node);
    }

    //重置寻路结果
    
    /**
     * @deprecated 
     */
    protected void resetNodes() {
        this.nodes.clear();
    }

    @Override
    public List<Node> getRoute() {
        return new ArrayList<>(this.nodes);
    }

    @Override
    public @Nullable Node getCurrentNode() {
        if (this.hasCurrentNode()) {
            return nodes.get(currentIndex);
        }
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasNext() {
        try {
            if (this.currentIndex + 1 < nodes.size()) {
                return this.nodes.get(this.currentIndex + 1) != null;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    @Override
    public @Nullable Node next() {
        if (this.hasNext()) {
            return this.nodes.get(++currentIndex);
        }
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasCurrentNode() {
        return currentIndex < this.nodes.size();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getNodeIndex() {
        return this.currentIndex;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setNodeIndex(int index) {
        this.currentIndex = index;
    }

    @Override
    public @Nullable Node getNode(int index) {
        if (index + 1 < nodes.size()) {
            return this.nodes.get(index);
        }
        return null;
    }
}
