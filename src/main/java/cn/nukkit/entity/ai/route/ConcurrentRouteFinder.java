package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.route.blockevaluator.IBlockEvaluator;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 异步路径查找抽象类 <br/>
 * 实现了此类的寻路器应当提供完整的异步寻路支持 <br/>
 * PNX中未使用此寻路方案，但保留以提供API <br/>
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class ConcurrentRouteFinder extends SimpleRouteFinder {

    //同步访问锁
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ConcurrentRouteFinder(IBlockEvaluator blockEvaluator) {
        super(blockEvaluator);
    }

    protected void addNode(Node node) {
        try {
            lock.writeLock().lock();
            nodes.add(node);
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected void addNode(ArrayList<Node> node) {
        try {
            lock.writeLock().lock();
            nodes.addAll(node);
        } finally {
            lock.writeLock().unlock();
        }
    }

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

    //异步查找路径
    public CompletableFuture<Void> asyncSearch() {
        return CompletableFuture.runAsync(this::search);
    }
}
