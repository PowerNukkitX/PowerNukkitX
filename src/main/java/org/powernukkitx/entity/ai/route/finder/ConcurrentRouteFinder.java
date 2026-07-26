package org.powernukkitx.entity.ai.route.finder;

import org.powernukkitx.entity.ai.route.data.Node;
import org.powernukkitx.entity.ai.route.posevaluator.IPosEvaluator;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract class for concurrent pathfinding <br/>
 * A pathfinder implementing this class should provide full asynchronous pathfinding support <br/>
 * This pathfinding approach is not used in PNX, but is kept to provide an API <br/>
 */


public abstract class ConcurrentRouteFinder extends SimpleRouteFinder {

    //synchronized access lock
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ConcurrentRouteFinder(IPosEvaluator blockEvaluator) {
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
     * Thread-safely get the found path information (cloned)
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
        } catch (Exception ignore) {
        }
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
    public int getNodeIndex() {
        return this.currentIndex;
    }

    @Override
    public void setNodeIndex(int index) {
        this.currentIndex = index;
    }

    //asynchronously search for a path
    public CompletableFuture<Void> asyncSearch() {
        return CompletableFuture.runAsync(this::search);
    }
}
