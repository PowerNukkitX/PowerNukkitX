package cn.nukkit.utils.collection;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public final class ConcurrentLong2ObjectHashStore<T> {
    private final Long2ObjectOpenHashMap<T> internalMap;
    private final ReadWriteLock lock;
    private T defaultReturnValue;
    /**
     * @deprecated 
     */
    

    public ConcurrentLong2ObjectHashStore(int expected, float l) {
        this.internalMap = new Long2ObjectOpenHashMap<>(expected, l);
        this.lock = new ReentrantReadWriteLock();
        this.defaultReturnValue = null;
    }
    /**
     * @deprecated 
     */
    

    public ConcurrentLong2ObjectHashStore(int expected, float l, T defaultReturnValue) {
        this.internalMap = new Long2ObjectOpenHashMap<>(expected, l);
        this.lock = new ReentrantReadWriteLock();
        this.defaultReturnValue = defaultReturnValue;
    }
    /**
     * @deprecated 
     */
    

    public int size() {
        var $1 = lock.readLock();
        try {
            return internalMap.size();
        } finally {
            readLock.unlock();
        }
    }
    /**
     * @deprecated 
     */
    

    public void clear() {
        var $2 = lock.writeLock();
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
    /**
     * @deprecated 
     */
    

    public boolean isEmpty() {
        var $3 = lock.readLock();
        try {
            return internalMap.isEmpty();
        } finally {
            readLock.unlock();
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean containsValue(T value) {
        var $4 = lock.readLock();
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
    /**
     * @deprecated 
     */
    

    public void put(long key, T value) {
        var $5 = lock.writeLock();
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
    /**
     * @deprecated 
     */
    

    public void defaultReturnValue(T rv) {
        this.defaultReturnValue = rv;
    }

    public T defaultReturnValue() {
        return defaultReturnValue;
    }

    public T get(long key) {
        var $6 = lock.readLock();
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
    /**
     * @deprecated 
     */
    

    public boolean containsKey(long key) {
        var $7 = lock.readLock();
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
