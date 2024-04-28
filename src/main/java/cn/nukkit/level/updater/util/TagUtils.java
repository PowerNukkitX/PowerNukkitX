package cn.nukkit.level.updater.util;

import cn.nukkit.nbt.tag.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TagUtils {

    public static Object toMutable(Tag immutable) {
        return switch (immutable) {
            case CompoundTag t -> {
                Map<String, Object> mutable = new LinkedHashMap<>();
                t.getTags().forEach((k, v) -> mutable.put(k, toMutable(v)));
                yield mutable;
            }
            case ListTag<?> t -> {
                List<Object> list = new ArrayList<>();
                t.getAll().forEach((v -> list.add(toMutable(v))));
                yield list;
            }
            case ByteTag t -> (byte) (t.parseValue() & 0xff);
            case ShortTag t -> (short) (t.parseValue() & 0xffff);
            default -> immutable.parseValue();
        };
    }

    public static Tag toImmutable(Object mutable) {
        return switch (mutable) {
            case Map<?, ?> map -> {
                CompoundTag compoundTag = new CompoundTag();
                map.forEach((k, v) -> {
                    if (k instanceof String stringKey) {
                        compoundTag.put(stringKey, toImmutable(v));
                    }
                });
                yield compoundTag;
            }
            case List<?> list -> {
                ListTag<Tag> listTag = new ListTag<>();
                list.forEach(v -> listTag.add(toImmutable(v)));
                yield listTag;
            }
            default -> byClass(mutable);
        };
    }

    private static Tag byClass(Object mutable) {
        return switch (mutable) {
            case Integer v -> new IntTag(v);
            case Byte v -> new ByteTag(v);
            case Short v -> new ShortTag(v);
            case Long v -> new LongTag(v);
            case Float v -> new FloatTag(v);
            case Double v -> new DoubleTag(v);
            case String v -> new StringTag(v);
            case byte[] v -> new ByteArrayTag(v);
            case int[] v -> new IntArrayTag(v);
            case null -> new EndTag();
            default -> throw new IllegalArgumentException("unhandled error in TagUtils");
        };
    }
}
