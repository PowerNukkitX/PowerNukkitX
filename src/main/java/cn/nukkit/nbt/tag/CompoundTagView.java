package cn.nukkit.nbt.tag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Allay Project 12/17/2023
 *
 * @author Cool_Loong
 */
public class CompoundTagView extends CompoundTag {
    private final CompoundTag delegate;
    /**
     * @deprecated 
     */
    

    public CompoundTagView(CompoundTag delegate) {
        this.delegate = delegate;
    }

    @Override
    public Collection<Tag> getAllTags() {
        return Collections.unmodifiableCollection(delegate.getAllTags());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return delegate.getId();
    }

    @Override
    public CompoundTag put(String name, Tag tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putByte(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putShort(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putInt(String name, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putLong(String name, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putFloat(String name, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putDouble(String name, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putString(@Nullable String name, @NotNull String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putByteArray(String name, byte[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putIntArray(String name, int[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putList(String name, ListTag<? extends Tag> listTag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putCompound(String name, CompoundTag value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompoundTag putBoolean(String string, boolean val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tag get(String name) {
        return delegate.get(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean contains(String name) {
        return delegate.contains(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsCompound(String name) {
        return delegate.containsCompound(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsString(String name) {
        return delegate.containsString(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsIntArray(String name) {
        return delegate.containsIntArray(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsByteArray(String name) {
        return delegate.containsByteArray(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsNumber(String name) {
        return delegate.containsNumber(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsList(String name) {
        return delegate.containsList(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsList(String name, byte type) {
        return delegate.containsList(name, type);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsByte(String name) {
        return delegate.containsByte(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsShort(String name) {
        return delegate.containsShort(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsInt(String name) {
        return delegate.containsInt(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsDouble(String name) {
        return delegate.containsDouble(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean containsFloat(String name) {
        return delegate.containsFloat(name);
    }

    @Override
    public CompoundTag remove(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Tag> T removeAndGet(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getByte(String name) {
        return delegate.getByte(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public short getShort(String name) {
        return delegate.getShort(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getInt(String name) {
        return delegate.getInt(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public long getLong(String name) {
        return delegate.getLong(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getFloat(String name) {
        return delegate.getFloat(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getDouble(String name) {
        return delegate.getDouble(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getString(String name) {
        return delegate.getString(name);
    }

    @Override
    public byte[] getByteArray(String name) {
        return delegate.getByteArray(name);
    }

    @Override
    public int[] getIntArray(String name) {
        return delegate.getIntArray(name);
    }

    @Override
    public CompoundTag getCompound(String name) {
        return delegate.getCompound(name);
    }

    @Override
    public ListTag<? extends Tag> getList(String name) {
        return delegate.getList(name);
    }

    @Override
    public <T extends Tag> ListTag<T> getList(String name, Class<T> type) {
        return delegate.getList(name, type);
    }

    @Override
    public Map<String, Tag> getTags() {
        return Collections.unmodifiableMap(delegate.getTags());
    }

    @Override
    public Map<String, Object> parseValue() {
        return delegate.parseValue();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean getBoolean(String name) {
        return delegate.getBoolean(name);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        return delegate.toString();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        return delegate.toSNBT();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT(int space) {
        return delegate.toSNBT(space);
    }


    @Override
    /**
     * @deprecated 
     */
    
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public LinkedCompoundTag copy() {
        return new LinkedCompoundTag(delegate.copy().getTags());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean exist(String name) {
        return delegate.exist(name);
    }
}
