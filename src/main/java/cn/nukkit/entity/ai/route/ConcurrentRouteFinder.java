package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 提供路径结果并发存储和读取的抽象类
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public abstract class ConcurrentRouteFinder implements IRouteFinder {
    //用于存储寻路结果的List
    protected ArrayList<Node> nodes = new ArrayList<>();

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
     * 线程安全地获取路径信息（cloned）
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
}
