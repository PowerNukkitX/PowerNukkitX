package org.powernukkitx.utils;

import lombok.extern.slf4j.Slf4j;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Stores dynamic properties grouped by namespace using NBT-compatible value types.
 *
 * @author Curse
 */
@Slf4j
public final class DynamicProperties {
    /**
     * Root tag name for dynamic properties.
     */
    public static final String ROOT = "DynamicProperties";
    /**
     * Maximum UTF-8 byte length for string properties.
     */
    public static final int MAX_STRING_BYTES = 32767;
    /**
     * Maximum absolute value for numeric properties.
     */
    public static final double NUMBER_ABS_MAX = 9_223_372_036_854_775_807d;

    private final Supplier<CompoundTag> reader;
    private final Consumer<CompoundTag> writer;

    /**
     * Creates a new DynamicProperties instance.
     *
     * @param reader value for this API
     * @param writer value for this API
     */
    public DynamicProperties(Supplier<CompoundTag> reader, Consumer<CompoundTag> writer) {
        this.reader = reader;
        this.writer = writer;
    }

    /**
     * Removes a registered value.
     *
     * @param namespace value for this API
     * @param key value for this API
     */
    public synchronized void remove(String namespace, String key) {
        CompoundTag root = readRoot();
        CompoundTag properties = getProperties(root, namespace);
        if (properties == null || !properties.contains(key)) return;

        properties.remove(key);
        saveProperties(root, namespace, properties);
    }

    /**
     * Clears values for a namespace.
     *
     * @param namespace value for this API
     */
    public synchronized void clear(String namespace) {
        CompoundTag root = readRoot();
        saveProperties(root, namespace, new CompoundTag());
    }

    /**
     * Sets a value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @param value value for this API
     */
    public synchronized void set(String namespace, String key, Double value) {
        if (value == null) {
            remove(namespace, key);
            return;
        }
        if (!isFiniteAndInRange(value)) {
            log.warn("DynamicProperty '{}' rejected: out of numeric bounds or non-finite (value={})", key, value);
            return;
        }

        CompoundTag root = readRoot();
        CompoundTag properties = ensureProperties(root, namespace);
        properties.putDouble(key, value);
        saveProperties(root, namespace, properties);
    }

    /**
     * Sets a value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @param value value for this API
     */
    public synchronized void set(String namespace, String key, Boolean value) {
        if (value == null) {
            remove(namespace, key);
            return;
        }

        CompoundTag root = readRoot();
        CompoundTag properties = ensureProperties(root, namespace);
        properties.putBoolean(key, value);
        saveProperties(root, namespace, properties);
    }

    /**
     * Sets a value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @param value value for this API
     */
    public synchronized void set(String namespace, String key, String value) {
        if (value == null) {
            remove(namespace, key);
            return;
        }
        if (!fitsUtf8Limit(value)) {
            log.warn("DynamicProperty '{}' rejected: string exceeds {} UTF-8 bytes", key, MAX_STRING_BYTES);
            return;
        }

        CompoundTag root = readRoot();
        CompoundTag properties = ensureProperties(root, namespace);
        properties.putString(key, value);
        saveProperties(root, namespace, properties);
    }

    /**
     * Sets a vector value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @param value value for this API
     */
    public synchronized void setVec3(String namespace, String key, Vector3 value) {
        if (value == null) {
            remove(namespace, key);
            return;
        }
        if (!isFiniteAndInRange(value.x) || !isFiniteAndInRange(value.y) || !isFiniteAndInRange(value.z)) {
            log.warn("DynamicProperty '{}' rejected: vec3 has component(s) out of bounds or non-finite (x={}, y={}, z={})",
                key, value.x, value.y, value.z);
            return;
        }

        ListTag<FloatTag> list = new ListTag<>();
        list.add(new FloatTag((float) value.x));
        list.add(new FloatTag((float) value.y));
        list.add(new FloatTag((float) value.z));

        CompoundTag root = readRoot();
        CompoundTag properties = ensureProperties(root, namespace);
        properties.putList(key, list);
        saveProperties(root, namespace, properties);
    }

    /**
     * Returns a double value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @return the requested value
     */
    public Double getDouble(String namespace, String key) {
        Object value = get(namespace, key);
        switch (value) {
            case Number number -> {
                return number.doubleValue();
            }
            case String string -> {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
            case null, default -> {
                return null;
            }
        }
    }

    /**
     * Returns an integer value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @return the requested value
     */
    public Integer getInt(String namespace, String key) {
        Double value = getDouble(namespace, key);
        return value == null ? null : (int) Math.floor(value);
    }

    /**
     * Returns a float value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @return the requested value
     */
    public Float getFloat(String namespace, String key) {
        Double value = getDouble(namespace, key);
        return value == null ? null : value.floatValue();
    }

    /**
     * Returns a boolean value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @return the requested value
     */
    public Boolean getBoolean(String namespace, String key) {
        Object value = get(namespace, key);
        if (value == null) return null;
        if (value instanceof Number number) return number.byteValue() != 0;

        if (value instanceof String string) {
            String valueString = string.trim().toLowerCase();
            if ("true".equals(valueString) || "1".equals(valueString)) return true;
            if ("false".equals(valueString) || "0".equals(valueString)) return false;
        }
        return null;
    }

    /**
     * Returns a string value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @return the requested value
     */
    public String getString(String namespace, String key) {
        Object value = get(namespace, key);
        return switch (value) {
            case Number number -> String.valueOf(number);
            case String string -> string;
            case null, default -> null;
        };
    }

    /**
     * Returns a vector value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @return the requested value
     */
    public Vector3 getVec3(String namespace, String key) {
        Object value = get(namespace, key);
        if (value instanceof List<?> list &&
            list.size() == 3 &&
            list.get(0) instanceof Float x &&
            list.get(1) instanceof Float y &&
            list.get(2) instanceof Float z) {
            return new Vector3(x, y, z);
        }
        return null;
    }

    /**
     * Returns a stored value.
     *
     * @param namespace value for this API
     * @param key value for this API
     * @return the requested value
     */
    public synchronized Object get(String namespace, String key) {
        CompoundTag properties = getProperties(readRoot(), namespace);
        if (properties == null || !properties.contains(key)) return null;
        return properties.get(key).parseValue();
    }

    private CompoundTag readRoot() {
        CompoundTag root = reader.get();
        return root == null ? new CompoundTag() : root;
    }

    private CompoundTag ensureProperties(CompoundTag root, String namespace) {
        return root.containsCompound(namespace) ? root.getCompound(namespace) : new CompoundTag();
    }

    private CompoundTag getProperties(CompoundTag root, String namespace) {
        return root.containsCompound(namespace) ? root.getCompound(namespace) : null;
    }

    private void saveProperties(CompoundTag root, String namespace, CompoundTag properties) {
        root.putCompound(namespace, properties);
        writer.accept(root);
    }

    private static boolean isFiniteAndInRange(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value) && Math.abs(value) <= NUMBER_ABS_MAX;
    }

    private static boolean fitsUtf8Limit(String value) {
        return value.getBytes(StandardCharsets.UTF_8).length <= MAX_STRING_BYTES;
    }
}
