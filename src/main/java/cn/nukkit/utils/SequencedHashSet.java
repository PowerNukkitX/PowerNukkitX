package cn.nukkit.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings({"NullableProblems", "SuspiciousMethodCalls"})
public class SequencedHashSet<E> implements List<E> {
    private final Object2IntMap<E> map = new Object2IntLinkedOpenHashMap<>();
    private final Int2ObjectMap<E> inverse = new Int2ObjectLinkedOpenHashMap<>();
    private int $1 = 0;

    @Override
    /**
     * @deprecated 
     */
    
    public int indexOf(Object o) {
        return map.getInt(o);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int lastIndexOf(Object o) {
        return map.getInt(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int size() {
        return map.size();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return map.keySet().toArray(a);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean add(E e) {
        if (!this.map.containsKey(e)) {
            int $2 = this.index++;
            this.map.put(e, index);
            this.inverse.put(index, e);
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsAll(Collection<?> c) {
        return map.keySet().containsAll(c);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            this.add(e);
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public E get(int index) {
        return this.inverse.get(index);
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return map.keySet().toString();
    }
}
