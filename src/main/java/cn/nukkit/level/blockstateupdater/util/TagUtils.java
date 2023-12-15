package cn.nukkit.level.blockstateupdater.util;

import cn.nukkit.nbt.tag.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TagUtils {

    public static Object toMutable(Tag immutable) {
        if (immutable instanceof CompoundTag compoundTag) {
            Map<String, Object> mutable = new LinkedHashMap<>();
            for (Map.Entry<String, Tag> entry : compoundTag.getTags().entrySet()) {
                mutable.put(entry.getKey(), toMutable(entry.getValue()));
            }
            return mutable;
        } else if (immutable instanceof ListTag<?> listTag) {
            List<Object> list = new ArrayList<>();
            for (Tag value : listTag.getAll()) {
                list.add(toMutable(value));
            }
            return list;
        } else if (immutable instanceof ByteTag tag) {
            return (byte) (tag.parseValue() & 0xff);
        } else if (immutable instanceof ShortTag tag) {
            return (short) (tag.parseValue() & 0xffff);
        } else if (immutable instanceof IntTag tag) {
            return tag.parseValue();
        } else if (immutable instanceof LongTag tag) {
            return tag.parseValue();
        } else if (immutable instanceof FloatTag tag) {
            return tag.parseValue();
        } else if (immutable instanceof DoubleTag tag) {
            return tag.parseValue();
        } else if (immutable instanceof ByteArrayTag tag) {
            return tag.parseValue();
        } else if (immutable instanceof IntArrayTag tag) {
            return tag.parseValue();
        } else if (immutable instanceof StringTag tag) {
            return tag.parseValue();
        }
        throw new IllegalArgumentException("unhandle error in TagUtil");
    }

    public static Tag toImmutable(Object mutable) {
        if (mutable instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) mutable;
            CompoundTag compoundTag = new CompoundTag();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                compoundTag.put(entry.getKey(), toImmutable(entry.getValue()));
            }
            return compoundTag;
        } else if (mutable instanceof List list) {
            ListTag<? extends Tag> listTag = new ListTag<>();
            for (Object value : list) {
                list.add(toImmutable(value));
            }
            return listTag;
        }
        return byClass(mutable);
    }

    private static Tag byClass(Object mutable) {
        if (mutable instanceof Integer v) {
            return new IntTag(v);
        } else if (mutable instanceof Byte v) {
            return new ByteTag(v);
        } else if (mutable instanceof Short v) {
            return new ShortTag(v);
        } else if (mutable instanceof Long v) {
            return new LongTag(v);
        } else if (mutable instanceof Float v) {
            return new FloatTag(v);
        } else if (mutable instanceof Double v) {
            return new DoubleTag(v);
        } else if (mutable instanceof String v) {
            return new StringTag("", v);
        } else if (mutable instanceof byte[] v) {
            return new ByteArrayTag(v);
        } else if (mutable instanceof int[] v) {
            return new IntArrayTag(v);
        } else if (mutable instanceof Void) {
            return new EndTag();
        }
        throw new IllegalArgumentException("unhandle error in TagUtil");
    }
}
