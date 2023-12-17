package cn.nukkit.nbt.tag;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintStream;
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

    public CompoundTagView(CompoundTag delegate) {
        this.delegate = delegate;
    }


    @Override
    public void write(NBTOutputStream dos) throws IOException {
        delegate.write(dos);
    }

    @Override
    public void load(NBTInputStream dis) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Tag> getAllTags() {
        return Collections.unmodifiableCollection(delegate.getAllTags());
    }

    @Override
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
    public CompoundTag putList(ListTag<? extends Tag> listTag) {
        throw new UnsupportedOperationException();
    }


    @Override
    public CompoundTag putList(String name, ListTag<? extends Tag> listTag) {
        throw new UnsupportedOperationException();
    }


    @Override
    public CompoundTag putCompound(CompoundTag value) {
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
    public boolean contains(String name) {
        return delegate.contains(name);
    }


    @Override
    public boolean containsCompound(String name) {
        return delegate.containsCompound(name);
    }


    @Override
    public boolean containsString(String name) {
        return delegate.containsString(name);
    }


    @Override
    public boolean containsIntArray(String name) {
        return delegate.containsIntArray(name);
    }


    @Override
    public boolean containsByteArray(String name) {
        return delegate.containsByteArray(name);
    }


    @Override
    public boolean containsNumber(String name) {
        return delegate.containsNumber(name);
    }


    @Override
    public boolean containsList(String name) {
        return delegate.containsList(name);
    }


    @Override
    public boolean containsList(String name, byte type) {
        return delegate.containsList(name, type);
    }


    @Override
    public boolean containsByte(String name) {
        return delegate.containsByte(name);
    }


    @Override
    public boolean containsShort(String name) {
        return delegate.containsShort(name);
    }


    @Override
    public boolean containsInt(String name) {
        return delegate.containsInt(name);
    }


    @Override
    public boolean containsDouble(String name) {
        return delegate.containsDouble(name);
    }


    @Override
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
    public int getByte(String name) {
        return delegate.getByte(name);
    }

    @Override
    public int getShort(String name) {
        return delegate.getShort(name);
    }

    @Override
    public int getInt(String name) {
        return delegate.getInt(name);
    }

    @Override
    public long getLong(String name) {
        return delegate.getLong(name);
    }

    @Override
    public float getFloat(String name) {
        return delegate.getFloat(name);
    }

    @Override
    public double getDouble(String name) {
        return delegate.getDouble(name);
    }

    @Override
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
    public boolean getBoolean(String name) {
        return delegate.getBoolean(name);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public String toSNBT() {
        return delegate.toSNBT();
    }

    @Override
    public String toSNBT(int space) {
        return delegate.toSNBT(space);
    }

    @Override
    public void print(String prefix, PrintStream out) {
        delegate.print(prefix, out);
    }

    @Override
    public Tag setName(String name) {
        return delegate.setName(name);
    }

    @Override
    public @NotNull String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public CompoundTagView copy() {
        return new CompoundTagView(delegate.copy());
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public void print(PrintStream out) {
        delegate.print(out);
    }

    @Override
    public boolean exist(String name) {
        return delegate.exist(name);
    }

    @Override
    public CompoundTagView clone() {
        return new CompoundTagView(delegate.clone());
    }
}
