package cn.nukkit.plugin.js.compiler;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public record JType(Type asmType) {
    public static JType of(Type type) {
        return new JType(type);
    }

    @Nullable
    public static JType ofClassName(String className) {
        try {
            return new JType(Type.getType(Class.forName(className)));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static JType of(String descriptor) {
        return new JType(Type.getType(descriptor));
    }

    public static JType of(Class<?> type) {
        return new JType(Type.getType(type));
    }

    public static JType of(Method method) {
        return new JType(Type.getType(method));
    }
}
