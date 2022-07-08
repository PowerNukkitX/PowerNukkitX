package cn.nukkit.entity.ai.route;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author zzz1999 daoge_cmd @ MobPlugin
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class RouteFinder {

    protected ArrayList<Node> nodes = new ArrayList<>();
    protected boolean finished = false;
    protected boolean searching = false;

    protected int current = 0;

    public EntityIntelligent entity;

    protected Vector3 start;
    protected Vector3 destination;

    protected Level level;

    protected boolean interrupt = false;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected boolean reachable = true;

    RouteFinder(EntityIntelligent entity) {
        Objects.requireNonNull(entity,"RouteFinder: entity can not be null");
        this.entity = entity;
        this.level = entity.getLevel();
    }

    public EntityIntelligent getEntity() {
        return entity;
    }

    public Vector3 getStart() {
        return this.start;
    }

    public void setStart(Vector3 start) {
        if (!this.isSearching()) {
            this.start = start;
        }
    }

    public Vector3 getDestination() {
        return this.destination;
    }

    public void setDestination(Vector3 destination) {
        this.destination = destination;
        if (this.isSearching()) {
            this.interrupt = true;
            this.research();
        }
    }

    public void setDestinationDirectly(Vector3 destination) {
        this.destination = destination;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isSearching() {
        return searching;
    }

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

    public boolean isReachable() {
        return reachable;
    }

    public Node getCurrentNode() {
        try {
            lock.readLock().lock();
            if (this.hasCurrentNode()) {
                return nodes.get(current);
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }

    }
    public boolean hasCurrentNode() {
        return current < this.nodes.size();
    }


    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getCurrent() {
        return this.current;
    }

    public boolean hasArrivedNode(Vector3 vec) {
        try {
            lock.readLock().lock();
            if (this.hasNext() &&  this.getCurrentNode().getVector3()!=null) {
                Vector3 cur = this.getCurrentNode().getVector3();
                return vec.getX() == cur.getX() && vec.getZ() == cur.getZ();
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void resetNodes() {
        try {
            this.lock.writeLock().lock();
            this.nodes.clear();
            this.current = 0;
            this.interrupt = false;
            this.destination = null;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public abstract boolean search();

    public void research() {
        this.resetNodes();
        this.search();
    }

    public boolean hasNext() {
        try {
            if (this.current + 1 < nodes.size()) {
                return this.nodes.get(this.current + 1) != null;
            }
        } catch (Exception ignore) {}
        return false;
    }

    public Vector3 next() {
        try {
            lock.readLock().lock();
            if (this.hasNext()) {
                return this.nodes.get(++current).getVector3();
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }

    }

    public boolean isInterrupted() {
        return this.interrupt;
    }

    public boolean interrupt() {
        return this.interrupt ^= true;
    }
}
