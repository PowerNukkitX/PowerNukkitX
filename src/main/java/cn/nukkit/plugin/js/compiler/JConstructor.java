package cn.nukkit.plugin.js.compiler;

import org.objectweb.asm.Type;

import java.util.Arrays;

public record JConstructor(JClassBuilder builder, String superDelegateName, String constructorDelegateName,
                           JType[] superTypes, JType... argTypes) {
    public Type[] argAsmTypes() {
        return Arrays.stream(this.argTypes()).map(JType::asmType).toArray(Type[]::new);
    }

    public Type[] superAsmTypes() {
        return Arrays.stream(this.superTypes()).map(JType::asmType).toArray(Type[]::new);
    }
}
