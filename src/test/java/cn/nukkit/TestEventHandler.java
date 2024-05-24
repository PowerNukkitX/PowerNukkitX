package cn.nukkit;

import cn.nukkit.event.Event;
import com.google.common.base.Preconditions;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TestEventHandler<T extends Event> {
    private final Type type;
    private final Class<T> actualType;

    protected TestEventHandler() {
        Class<?> parameterizedTypeReferenceSubclass = findParameterizedTypeReferenceSubclass(this.getClass());
        Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();
        Preconditions.checkArgument(type instanceof ParameterizedType, "Type must be a parameterized type");
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Preconditions.checkArgument(actualTypeArguments.length == 1, "Number of type arguments must be 1");    // 设置结果
        this.type = actualTypeArguments[0];
        Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        Preconditions.checkArgument(actualTypeArgument instanceof Class<?>, "Type must be a class type");
        this.actualType = (Class<T>) actualTypeArgument;
    }

    private static Class<?> findParameterizedTypeReferenceSubclass(Class<?> child) {
        Class<?> parent = child.getSuperclass();
        if (Object.class == parent) {
            throw new IllegalStateException("Expected ParameterizedTypeReference superclass");
        } else {
            return parent == TestEventHandler.class ? child : findParameterizedTypeReferenceSubclass(parent);
        }
    }

    public abstract void handle(T event);

    public Class<T> getEventClass() {
        return actualType;
    }
}
