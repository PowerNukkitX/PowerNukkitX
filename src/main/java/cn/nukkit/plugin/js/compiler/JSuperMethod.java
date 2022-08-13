package cn.nukkit.plugin.js.compiler;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.util.Arrays;

public record JSuperMethod(JClassBuilder builder, String methodName, JType returnType, JType... argTypes) {
    @NotNull
    public Type[] argAsmTypes() {
        return Arrays.stream(this.argTypes()).map(JType::asmType).toArray(Type[]::new);
    }

    @NotNull
    public Type returnAsmType() {
        return returnType.asmType();
    }
}
