package cn.nukkit.utils.collection;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ConcurrentLong2ObjectHashStore<T> {
    private final Long2ObjectOpenHashMap<T> internalMap;
    private final ReadWriteLock lock;
    private T defaultReturnValue;

    public ConcurrentLong2ObjectHashStore(int expected, float l) {
        this.internalMap = new Long2ObjectOpenHashMap<>(expected, l);
        this.lock = new ReentrantReadWriteLock();
        this.defaultReturnValue = null;
    }

    public ConcurrentLong2ObjectHashStore(int expected, float l, T defaultReturnValue) {
        this.internalMap = new Long2ObjectOpenHashMap<>(expected, l);
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

    public void put(long key, T value) {
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

    public T get(long key) {
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

    public boolean containsKey(long key) {
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
