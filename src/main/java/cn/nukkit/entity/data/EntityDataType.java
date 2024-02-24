package cn.nukkit.entity.data;

import java.util.function.Function;

public class EntityDataType<T> {
    private final String name;
    private final Class<?> type;
    private final int value;
    private final Function<T, ?> transformer;
    private final T defaultValue;

    public EntityDataType(T type, String name, int value) {
        this.name = name;
        this.type = type.getClass();
        this.defaultValue = type;
        this.value = value;
        this.transformer = Function.identity();
    }

    public EntityDataType(T type, String name, int value, Function<T, ?> transformer) {
        this.name = name;
        this.type = type.getClass();
        this.defaultValue = type;
        this.value = value;
        this.transformer = transformer;
    }

    public boolean isInstance(Object value) {
        return type.isInstance(value);
    }

    public String getTypeName() {
        return this.type.getTypeName();
    }

    public Class<?> getType() {
        return type;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Function<Object, Object> getTransformer() {
        return (Function<Object, Object>) transformer;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
}
