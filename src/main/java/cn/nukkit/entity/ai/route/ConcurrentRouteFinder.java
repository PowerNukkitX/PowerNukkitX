package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 提供node的并发存储的抽象类
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public abstract class ConcurrentRouteFinder implements IRouteFinder {
    //用于存储寻路结果的List
    protected ArrayList<Node> nodes = new ArrayList<>();

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void addNode(Node node) {
        try {
            lock.writeLock().lock();
            nodes.add(node);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addNode(ArrayList<Node> node) {
        try {
            lock.writeLock().lock();
            nodes.addAll(node);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void resetNodes() {
        try {
            this.lock.writeLock().lock();
            this.nodes.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
