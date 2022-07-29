package cn.nukkit.utils.collection;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public final class ConcurrentInt2ObjectHashStore<T> {
    private final Int2ObjectOpenHashMap<T> internalMap;
    private final ReadWriteLock lock;
    private T defaultReturnValue;

    public ConcurrentInt2ObjectHashStore(int expected, float l) {
        this.internalMap = new Int2ObjectOpenHashMap<>(expected, l);
        this.lock = new ReentrantReadWriteLock();
        this.defaultReturnValue = null;
    }

    public ConcurrentInt2ObjectHashStore(int expected, float l, T defaultReturnValue) {
        this.internalMap = new Int2ObjectOpenHashMap<>(expected, l);
        this.lock = new ReentrantReadWriteLock();
        this.defaultReturnValue = defaultReturnValue;
    }

    public int size() {
        var readLock = lock.readLock();
        try {
            return internalMap.size();
        } finally {
            readLock.unlock();
        }
    }

    public void clear() {
        var writeLock = lock.writeLock();
        while (true) {
            if (writeLock.tryLock()) {
                try {
                    internalMap.clear();
                    break;
                } finally {
                    writeLock.unlock();
                }
            }
        }
    }

    public boolean isEmpty() {
        var readLock = lock.readLock();
        try {
            return internalMap.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsValue(T value) {
        var readLock = lock.readLock();
        while (true) {
            if (readLock.tryLock()) {
                try {
                    return internalMap.containsValue(value);
                } finally {
                    readLock.unlock();
                }
            }
        }
    }

    public void put(int key, T value) {
        var writeLock = lock.writeLock();
        while (true) {
            if (writeLock.tryLock()) {
                try {
                    internalMap.put(key, value);
                    break;
                } finally {
                    writeLock.unlock();
                }
            }
        }
    }

    public void defaultReturnValue(T rv) {
        this.defaultReturnValue = rv;
    }

    public T defaultReturnValue() {
        return defaultReturnValue;
    }

    public T get(int key) {
        var readLock = lock.readLock();
        while (true) {
            if (readLock.tryLock()) {
                try {
                    return internalMap.get(key);
                } finally {
                    readLock.unlock();
                }
            }
        }
    }

    public boolean containsKey(int key) {
        var readLock = lock.readLock();
        while (true) {
            if (readLock.tryLock()) {
                try {
                    return internalMap.containsKey(key);
                } finally {
                    readLock.unlock();
                }
            }
        }
    }
}
