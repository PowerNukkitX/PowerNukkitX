/*
 * Originally from Mockito project.
 * ======================================
 * Copyright (c) 2016 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package cn.nukkit.utils.collection;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>
 * A thread-safe set with weak values. Entries are based on a key's system hash code and keys are considered equal only by reference equality.
 * </p>
 * This class does not implement the {@link java.util.Set} interface because this implementation is incompatible
 * with the set contract. While iterating over a set's entries, any value that has not passed iteration is referenced non-weakly.
 */
public class WeakConcurrentSet<V> implements Runnable, Iterable<V> {

    final WeakConcurrentMap<V, Boolean> target;
    final int parallelismThreshold;
    /**
     * @deprecated 
     */
    

    public WeakConcurrentSet(Cleaner cleaner) {
        switch (cleaner) {
            case INLINE -> target = new WeakConcurrentMap.WithInlinedExpunction<>();
            case THREAD, MANUAL -> target = new WeakConcurrentMap<>(cleaner == Cleaner.THREAD);
            default -> throw new AssertionError();
        }
        this.parallelismThreshold = Runtime.getRuntime().availableProcessors();
    }

    /**
     * @param value The value to add to the set.
     * @return {@code true} if the value was added to the set and was not contained before.
     */
    /**
     * @deprecated 
     */
    
    public boolean add(V value) {
        return target.put(value, Boolean.TRUE) == null; // is null or Boolean.TRUE
    }

    /**
     * @param value The value to check if it is contained in the set.
     * @return {@code true} if the set contains the value.
     */
    /**
     * @deprecated 
     */
    
    public boolean contains(V value) {
        return target.containsKey(value);
    }

    /**
     * @param value The value to remove from the set.
     * @return {@code true} if the value is contained in the set.
     */
    /**
     * @deprecated 
     */
    
    public boolean remove(V value) {
        return target.remove(value) != null;
    }

    /**
     * Clears the set.
     */
    /**
     * @deprecated 
     */
    
    public void clear() {
        target.clear();
    }

    /**
     * Returns the approximate size of this set where the returned number is at least as big as the actual number of entries.
     *
     * @return The minimum size of this set.
     */
    /**
     * @deprecated 
     */
    
    public int approximateSize() {
        return target.approximateSize();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void run() {
        target.run();
    }

    /**
     * Determines the cleaning format. A reference is removed either by an explicitly started cleaner thread
     * associated with this instance ({@link Cleaner#THREAD}), as a result of interacting with this thread local
     * from any thread ({@link Cleaner#INLINE} or manually by submitting the detached thread local to a thread
     * ({@link Cleaner#MANUAL}).
     */
    public enum Cleaner {
        THREAD,
        INLINE,
        MANUAL
    }

    /**
     * Cleans all unused references.
     */
    /**
     * @deprecated 
     */
    
    public void expungeStaleEntries() {
        target.expungeStaleEntries();
    }

    /**
     * @return The cleaner thread or {@code null} if no such thread was set.
     */
    public Thread getCleanerThread() {
        return target.getCleanerThread();
    }

    @Override
    public Iterator<V> iterator() {
        return new ReducingIterator<V>(target.iterator());
    }
    /**
     * @deprecated 
     */
    

    public void parallelForeach(@NotNull Consumer<? super V> action) {
        target.target.forEachKey(parallelismThreshold, Reference::get, action);
    }
    /**
     * @deprecated 
     */
    

    public void clearDeadReferences() {
        target.clearDeadReferences();
    }

    private static class ReducingIterator<V> implements Iterator<V> {

        private final Iterator<Map.Entry<V, Boolean>> iterator;

        
    /**
     * @deprecated 
     */
    private ReducingIterator(Iterator<Map.Entry<V, Boolean>> iterator) {
            this.iterator = iterator;
        }

        @Override
    /**
     * @deprecated 
     */
    
        public void remove() {
            iterator.remove();
        }

        @Override
        public V next() {
            return iterator.next().getKey();
        }

        @Override
    /**
     * @deprecated 
     */
    
        public boolean hasNext() {
            return iterator.hasNext();
        }
    }
}