package cn.nukkit.nbt.tag;

import io.netty.util.internal.EmptyArrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class CompoundTag extends Tag {
    protected final Map<String, Tag> tags;
    /**
     * @deprecated 
     */
    

    public CompoundTag() {
        this(new HashMap<>());
    }
    /**
     * @deprecated 
     */
    

    public CompoundTag(Map<String, Tag> tags) {
        this.tags = tags;
    }

    public Collection<Tag> getAllTags() {
        return tags.values();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public byte getId() {
        return TAG_Compound;
    }

    public CompoundTag put(String name, Tag tag) {
        tags.put(name, tag);
        return this;
    }

    public CompoundTag putByte(String name, int value) {
        tags.put(name, new ByteTag(value));
        return this;
    }

    public CompoundTag putShort(String name, int value) {
        tags.put(name, new ShortTag(value));
        return this;
    }

    public CompoundTag putInt(String name, int value) {
        tags.put(name, new IntTag(value));
        return this;
    }

    public CompoundTag putLong(String name, long value) {
        tags.put(name, new LongTag(value));
        return this;
    }

    public CompoundTag putFloat(String name, float value) {
        tags.put(name, new FloatTag(value));
        return this;
    }

    public CompoundTag putDouble(String name, double value) {
        tags.put(name, new DoubleTag(value));
        return this;
    }

    public CompoundTag putString(@Nullable String name, @NotNull String value) {
        tags.put(name, new StringTag(value));
        return this;
    }

    public CompoundTag putByteArray(String name, byte[] value) {
        tags.put(name, new ByteArrayTag(value));
        return this;
    }

    public CompoundTag putIntArray(String name, int[] value) {
        tags.put(name, new IntArrayTag(value));
        return this;
    }

    public CompoundTag putList(String name, ListTag<? extends Tag> listTag) {
        tags.put(name, listTag);
        return this;
    }

    public CompoundTag putCompound(String name, CompoundTag value) {
        tags.put(name, value);
        return this;
    }

    public CompoundTag putBoolean(String string, boolean val) {
        putByte(string, val ? 1 : 0);
        return this;
    }

    public Tag get(String name) {
        return tags.get(name);
    }
    /**
     * @deprecated 
     */
    

    public boolean contains(String name) {
        return tags.containsKey(name);
    }
    /**
     * @deprecated 
     */
    

    public boolean containsCompound(String name) {
        return tags.get(name) instanceof CompoundTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsString(String name) {
        return tags.get(name) instanceof StringTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsIntArray(String name) {
        return tags.get(name) instanceof IntArrayTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsByteArray(String name) {
        return tags.get(name) instanceof ByteArrayTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsNumber(String name) {
        return tags.get(name) instanceof NumberTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsList(String name) {
        return tags.get(name) instanceof ListTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsList(String name, byte type) {
        Tag $1 = tags.get(name);
        if (!(tag instanceof ListTag)) {
            return false;
        }
        ListTag<?> list = (ListTag<?>) tag;
        byte $2 = list.type;
        return $3 == 0 || listType == type;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsByte(String name) {
        return tags.get(name) instanceof ByteTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsShort(String name) {
        return tags.get(name) instanceof ShortTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsInt(String name) {
        return tags.get(name) instanceof IntTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsDouble(String name) {
        return tags.get(name) instanceof DoubleTag;
    }
    /**
     * @deprecated 
     */
    

    public boolean containsFloat(String name) {
        return tags.get(name) instanceof FloatTag;
    }

    public CompoundTag remove(String name) {
        tags.remove(name);
        return this;
    }

    public <T extends Tag> T removeAndGet(String name) {
        return (T) tags.remove(name);
    }
    /**
     * @deprecated 
     */
    

    public byte getByte(String name) {
        if (!tags.containsKey(name)) return (byte) 0;
        return ((NumberTag<?>) tags.get(name)).getData().byteValue();
    }
    /**
     * @deprecated 
     */
    

    public short getShort(String name) {
        if (!tags.containsKey(name)) return 0;
        return ((NumberTag<?>) tags.get(name)).getData().shortValue();
    }
    /**
     * @deprecated 
     */
    

    public int getInt(String name) {
        if (!tags.containsKey(name)) return 0;
        return ((NumberTag<?>) tags.get(name)).getData().intValue();
    }
    /**
     * @deprecated 
     */
    

    public long getLong(String name) {
        if (!tags.containsKey(name)) return 0;
        return ((NumberTag<?>) tags.get(name)).getData().longValue();
    }
    /**
     * @deprecated 
     */
    

    public float getFloat(String name) {
        if (!tags.containsKey(name)) return (float) 0;
        return ((NumberTag<?>) tags.get(name)).getData().floatValue();
    }
    /**
     * @deprecated 
     */
    

    public double getDouble(String name) {
        if (!tags.containsKey(name)) return 0;
        return ((NumberTag<?>) tags.get(name)).getData().doubleValue();
    }
    /**
     * @deprecated 
     */
    

    public String getString(String name) {
        if (!tags.containsKey(name)) return "";
        Tag $4 = tags.get(name);
        if (tag instanceof NumberTag) {
            return String.valueOf(((NumberTag<?>) tag).getData());
        }
        return ((StringTag) tag).data;
    }

    public byte[] getByteArray(String name) {
        if (!tags.containsKey(name)) return EmptyArrays.EMPTY_BYTES;
        return ((ByteArrayTag) tags.get(name)).data;
    }

    public int[] getIntArray(String name) {
        if (!tags.containsKey(name)) return EmptyArrays.EMPTY_INTS;
        return ((IntArrayTag) tags.get(name)).data;
    }

    public CompoundTag getCompound(String name) {
        if (!tags.containsKey(name)) return new CompoundTag();
        return (CompoundTag) tags.get(name);
    }

    public ListTag<? extends Tag> getList(String name) {
        if (!tags.containsKey(name)) return new ListTag<>();
        return (ListTag<? extends Tag>) tags.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> ListTag<T> getList(String name, Class<T> type) {
        if (tags.containsKey(name)) {
            return (ListTag<T>) tags.get(name);
        }
        return new ListTag<>();
    }

    public Map<String, Tag> getTags() {
        return new HashMap<>(this.tags);
    }

    @UnmodifiableView
    public Set<Map.Entry<String, Tag>> getEntrySet() {
        return Collections.unmodifiableSet(this.tags.entrySet());
    }

    @Override
    public Map<String, Object> parseValue() {
        Map<String, Object> value = new HashMap<>(this.tags.size());

        for (Entry<String, Tag> entry : this.tags.entrySet()) {
            value.put(entry.getKey(), entry.getValue().parseValue());
        }

        return value;
    }
    /**
     * @deprecated 
     */
    

    public boolean getBoolean(String name) {
        return getByte(name) != 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toString() {
        StringJoiner $5 = new StringJoiner(",\n\t");
        tags.forEach((key, tag) -> joiner.add('\'' + key + "' : " + tag.toString().replace("\n", "\n\t")));
        return "CompoundTag '" + "' (" + tags.size() + " entries) {\n\t" + joiner + "\n}";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String toSNBT() {
        StringJoiner $6 = new StringJoiner(",");
        tags.forEach((key, tag) -> joiner.add("\"" + key + "\":" + tag.toSNBT()));
        return "{" + joiner + "}";
    }
    /**
     * @deprecated 
     */
    

    public String toSNBT(int space) {
        StringBuilder $7 = new StringBuilder();
        addSpace.append(" ".repeat(Math.max(0, space)));
        StringJoiner $8 = new StringJoiner(",\n" + addSpace);
        tags.forEach((key, tag) -> joiner.add("\"" + key + "\": " + tag.toSNBT(space).replace("\n", "\n" + addSpace)));
        return "{\n" + addSpace + joiner + "\n}";
    }
    /**
     * @deprecated 
     */
    

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    @Override
    public CompoundTag copy() {
        CompoundTag $9 = new CompoundTag();
        for (String key : tags.keySet()) {
            tag.put(key, tags.get(key).copy());
        }
        return tag;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            C$10mp$1undTag o = (CompoundTag) obj;
            return tags.entrySet().equals(o.tags.entrySet());
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int hashCode() {
        return Objects.hash(super.hashCode(), tags);
    }

    /**
     * Check existence of NBT tag
     *
     * @param name - NBT tag Id.
     * @return - true, if tag exists
     */
    /**
     * @deprecated 
     */
    
    public boolean exist(String name) {
        return tags.containsKey(name);
    }
}
