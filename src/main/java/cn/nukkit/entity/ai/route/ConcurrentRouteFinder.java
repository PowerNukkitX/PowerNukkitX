package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.route.blockevaluator.IBlockEvaluator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 异步路径查找抽象类
 * 实现了此类的寻路器应当提供完整的异步寻路支持
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public abstract class ConcurrentRouteFinder implements IRouteFinder {
    //用于存储寻路结果的List
    protected ArrayList<Node> nodes = new ArrayList<>();

    //Node索引
    protected int currentIndex = 0;

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    //方块评估器
    protected IBlockEvaluator blockEvaluator;

    public ConcurrentRouteFinder(IBlockEvaluator blockEvaluator) {
        this.blockEvaluator = blockEvaluator;
    }

    /**
     * 添加寻路结果节点
     */
    protected void addNode(Node node) {
        try {
            lock.writeLock().lock();
            nodes.add(node);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 批量添加寻路结果节点
     */
    protected void addNode(ArrayList<Node> node) {
        try {
            lock.writeLock().lock();
            nodes.addAll(node);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 重置结果
     */
    protected void resetNodes() {
        try {
            this.lock.writeLock().lock();
            this.nodes.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * 线程安全地获取查找到的路径信息（cloned）
     */
    @Override
    public ArrayList<Node> getRoute() {
        ArrayList<Node> clone = new ArrayList<>();
        try {
            this.lock.writeLock().lock();
            for (Node node : this.nodes) {
                clone.add(node);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
        return clone;
    }

    @Override
    public Node getCurrentNode() {
        try {
            lock.readLock().lock();
            if (this.hasCurrentNode()) {
                return nodes.get(currentIndex);
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
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
    public Node next() {
        try {
            lock.readLock().lock();
            if (this.hasNext()) {
                return this.nodes.get(++currentIndex);
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }

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

    public CompletableFuture<Void> asyncSearch() {
        return CompletableFuture.runAsync(this::search);
    }
}
