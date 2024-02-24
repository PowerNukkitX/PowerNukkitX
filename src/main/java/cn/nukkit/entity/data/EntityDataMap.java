package cn.nukkit.entity.data;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.nukkit.entity.data.EntityDataTypes.FLAGS;
import static cn.nukkit.entity.data.EntityDataTypes.FLAGS_2;
import static com.google.common.base.Preconditions.checkNotNull;


public final class EntityDataMap implements Map<EntityDataType<?>, Object> {
    private final Map<EntityDataType<?>, Object> map = new LinkedHashMap<>();

    @NonNull
    public EnumSet<EntityFlag> getOrCreateFlags() {
        EnumSet<EntityFlag> flags = get(FLAGS);
        if (flags == null) {
            flags = get(FLAGS_2);
            if (flags == null) {
                flags = EnumSet.noneOf(EntityFlag.class);
            }
            this.putFlags(flags);
        }
        return flags;
    }

    public EnumSet<EntityFlag> getFlags() {
        return get(FLAGS);
    }

    public EntityFlag setFlag(EntityFlag flag, boolean value) {
        Objects.requireNonNull(flag, "flag");
        EnumSet<EntityFlag> flags = this.getOrCreateFlags();
        if (value) {
            flags.add(flag);
        } else {
            flags.remove(flag);
        }

        return flag;
    }

    public boolean existFlag(EntityFlag flag) {
        Objects.requireNonNull(flag, "flag");
        EnumSet<EntityFlag> flags = this.getOrCreateFlags();
        return flags.contains(flag);
    }

    public EnumSet<EntityFlag> putFlags(EnumSet<EntityFlag> flags) {
        Objects.requireNonNull(flags, "flags");
        this.map.put(FLAGS, flags);
        this.map.put(FLAGS_2, flags);
        return flags;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(EntityDataType<T> type) {
        return (T) this.map.getOrDefault(type, type.getDefaultValue());
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(EntityDataType<T> type, T defaultValue) {
        Objects.requireNonNull(type, "type");
        Object object = this.map.getOrDefault(type, defaultValue);
        try {
            return (T) object;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public <T> void putType(EntityDataType<T> type, T value) {
        this.put(type, value);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return this.map.get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object put(EntityDataType<?> key, Object value) {
        checkNotNull(key, "type");
        checkNotNull(value, "value was null for %s", key);
        if (key == FLAGS || key == FLAGS_2) {
            return this.putFlags((EnumSet<EntityFlag>) value);
        }
        if (Number.class.isAssignableFrom(value.getClass())) {
            Class<?> type = key.getType();
            Number number = (Number) value;
            if (type == long.class || type == Long.class) {
                value = number.longValue();
            } else if (type == int.class || type == Integer.class) {
                value = number.intValue();
            } else if (type == short.class || type == Short.class) {
                value = number.shortValue();
            } else if (type == byte.class || type == Byte.class) {
                value = number.byteValue();
            } else if (type == float.class || type == Float.class) {
                value = number.floatValue();
            } else if (type == double.class || type == Double.class) {
                value = number.doubleValue();
            }
        }
        return this.map.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(@NonNull Map<? extends EntityDataType<?>, ?> map) {
        checkNotNull(map, "map");
        this.map.putAll(map);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @NonNull
    @Override
    public Set<EntityDataType<?>> keySet() {
        return this.map.keySet();
    }

    @NonNull
    @Override
    public Collection<Object> values() {
        return this.map.values();
    }

    @NonNull
    @Override
    public Set<Entry<EntityDataType<?>, Object>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityDataMap that = (EntityDataMap) o;
        return this.map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    public EntityDataMap copy(EntityDataType<?>... entityDataTypes) {
        EntityDataMap entityDataMap = new EntityDataMap();
        for (var t : entityDataTypes) {
            Object o = this.get(t);
            if (o != null) {
                entityDataMap.put(t, o);
            }
        }
        return entityDataMap;
    }

    @Override
    public String toString() {
        Iterator<Entry<EntityDataType<?>, Object>> i = map.entrySet().iterator();
        if (!i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        while (i.hasNext()) {
            Entry<EntityDataType<?>, Object> e = i.next();
            EntityDataType<?> key = e.getKey();
            if (key == FLAGS_2) continue; // We don't want this to be visible.
            String stringVal = e.getValue().toString();
            sb.append(key.toString()).append('=').append(stringVal);
            if (!i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
        return sb.toString();
    }
}
