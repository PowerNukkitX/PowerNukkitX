package cn.nukkit.plugin.js.compiler;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.util.Arrays;

public record JMethod(JClassBuilder builder, String methodName, String delegateName, JType returnType, JType... argTypes) {
    public JMethod(JClassBuilder builder, String name, JType returnType, JType... argTypes) {
        this(builder, name, name, returnType, argTypes);
    }

    @NotNull
    public Type[] argAsmTypes() {
        return Arrays.stream(this.argTypes()).map(JType::asmType).toArray(Type[]::new);
    }

    @NotNull
    public Type returnAsmType() {
        return returnType.asmType();
    }
}
